package aid.models;

public class Song {
    public int id;
    public String title;
    public String artist;
    public String file;
    public String cover;

    @Override
    public String toString() {
        return title + " by " + artist;
    }
}
