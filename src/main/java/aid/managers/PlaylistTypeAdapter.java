package aid.managers;

import aid.models.Playlist;
import aid.models.SmartPlaylist;
import aid.models.StandardPlaylist;
import com.google.gson.*;
import java.lang.reflect.Type;
import java.util.List;
import com.google.gson.reflect.TypeToken;

public class PlaylistTypeAdapter implements JsonSerializer<Playlist>, JsonDeserializer<Playlist> {

    private static final String TYPE_FIELD = "type";
    private static final String GENRE_CRITERIA_FIELD = "genreCriteria"; // Nama field baru

    @Override
    public JsonElement serialize(Playlist src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", src.getId());
        jsonObject.addProperty("name", src.getName());
        jsonObject.addProperty("ownerId", src.getOwnerId());
        
        // Untuk SmartPlaylist, songIds mungkin kosong, tapi kita tetap simpan
        // Untuk StandardPlaylist, songIds adalah lagu yang dipilih
        JsonArray songIdsArray = new JsonArray();
        src.getSongIds().forEach(songIdsArray::add);
        jsonObject.add("songIds", songIdsArray); // Selalu simpan songIds

        jsonObject.addProperty(TYPE_FIELD, src.getType());

        // --- PERUBAHAN: Simpan genreCriteria jika SmartPlaylist ---
        if (src instanceof SmartPlaylist) {
            SmartPlaylist smartPlaylist = (SmartPlaylist) src;
            jsonObject.addProperty(GENRE_CRITERIA_FIELD, smartPlaylist.getGenreCriteria());
        }
        // --- AKHIR PERUBAHAN ---

        return jsonObject;
    }

    @Override
    public Playlist deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        String type = jsonObject.get(TYPE_FIELD).getAsString();
        String id = jsonObject.get("id").getAsString();
        String name = jsonObject.get("name").getAsString();
        String ownerId = jsonObject.get("ownerId").getAsString();

        Type listType = new TypeToken<List<String>>() {}.getType();
        List<String> songIds = context.deserialize(jsonObject.get("songIds"), listType); // Deserialize songIds

        switch (type) {
            case "Standard":
                return new StandardPlaylist(id, name, ownerId, songIds);
            case "Smart":
                // --- PERUBAHAN: Deserialisasi genreCriteria untuk SmartPlaylist ---
                String genreCriteria = null;
                if (jsonObject.has(GENRE_CRITERIA_FIELD) && !jsonObject.get(GENRE_CRITERIA_FIELD).isJsonNull()) {
                    genreCriteria = jsonObject.get(GENRE_CRITERIA_FIELD).getAsString();
                }
                // SmartPlaylist akan mengisi songIds-nya secara dinamis.
                // Konstruktor SmartPlaylist akan menerima genreCriteria.
                // songIds yang dideserialisasi di sini bisa diabaikan atau digunakan sebagai hint.
                return new SmartPlaylist(id, name, ownerId, songIds, genreCriteria); // Teruskan songIds dan genreCriteria
                // --- AKHIR PERUBAHAN ---
            default:
                throw new JsonParseException("Unknown playlist type: " + type);
        }
    }
}