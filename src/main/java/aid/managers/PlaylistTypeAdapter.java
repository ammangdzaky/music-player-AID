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

    @Override
    public JsonElement serialize(Playlist src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", src.getId());
        jsonObject.addProperty("name", src.getName());
        jsonObject.addProperty("ownerId", src.getOwnerId());
        JsonArray songIdsArray = new JsonArray();
        src.getSongIds().forEach(songIdsArray::add);
        jsonObject.add("songIds", songIdsArray);
        jsonObject.addProperty(TYPE_FIELD, src.getType());

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
        List<String> songIds = context.deserialize(jsonObject.get("songIds"), listType);

        switch (type) {
            case "Standard":
                return new StandardPlaylist(id, name, ownerId, songIds);
            case "Smart":
                return new SmartPlaylist(id, name, ownerId, songIds);
            default:
                throw new JsonParseException("Unknown playlist type: " + type);
        }
    }
}
