package aid.models;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SmartPlaylist extends Playlist {
    // --- PERUBAHAN: Tambahkan field untuk kriteria Smart Playlist ---
    private String genreCriteria; // Contoh: "Pop", "Rock", dll.
    // --- AKHIR PERUBAHAN ---

    public SmartPlaylist(String id, String name, String ownerId, List<String> songIds) {
        super(id, name, ownerId, songIds);
        // Penting: SmartPlaylist tidak akan menyimpan songIds secara langsung saat pembuatan,
        // melainkan akan mengisinya berdasarkan kriteria saat diakses.
        // Jika songIds dari superclass digunakan untuk penyimpanan, itu perlu dikelola.
        // Untuk tujuan ini, kita akan selalu mengisinya dinamis.
        this.songIds = new ArrayList<>(); // Pastikan kosong saat init, akan diisi dinamis
    }

    // Konstruktor tambahan untuk deserialisasi dengan genreCriteria
    // Gson akan menggunakan ini jika field genreCriteria ada di JSON
    public SmartPlaylist(String id, String name, String ownerId, List<String> songIds, String genreCriteria) {
        super(id, name, ownerId, songIds); // songIds akan tetap kosong atau diabaikan
        this.genreCriteria = genreCriteria;
        this.songIds = new ArrayList<>(); // Smart playlist song IDs are dynamic
    }

    @Override
    public void addSong(String songId) {
        // SmartPlaylist tidak menambahkan lagu secara manual.
        // Anda bisa throw UnsupportedOperationException atau biarkan kosong.
        // System.out.println("Warning: Cannot manually add song to Smart Playlist.");
    }

    @Override
    public void removeSong(String songId) {
        // SmartPlaylist tidak menghapus lagu secara manual.
        // System.out.println("Warning: Cannot manually remove song from Smart Playlist.");
    }

    // --- PERUBAHAN: Getter dan Setter untuk genreCriteria ---
    public String getGenreCriteria() {
        return genreCriteria;
    }

    public void setGenreCriteria(String genreCriteria) {
        this.genreCriteria = genreCriteria;
    }
    // --- AKHIR PERUBAHAN ---

    @Override
    public String getType() {
        return "Smart";
    }

    // --- PERUBAHAN: Metode untuk mengisi lagu berdasarkan kriteria ---
    // Metode ini akan dipanggil oleh DataManager atau ProfileView saat smart playlist diakses
    public List<String> generateSongIds(List<Song> allSongs) {
        if (genreCriteria == null || genreCriteria.isEmpty()) {
            return new ArrayList<>();
        }
        return allSongs.stream()
                .filter(song -> song.getGenre() != null && song.getGenre().equalsIgnoreCase(genreCriteria))
                .map(song -> String.valueOf(song.getId()))
                .collect(Collectors.toList());
    }
    // --- AKHIR PERUBAHAN ---

    @Override
    public String toString() {
        return "Smart" + super.toString() + " [Genre Criteria: " + genreCriteria + "]";
    }
}