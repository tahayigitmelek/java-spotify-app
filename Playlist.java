package advisor;


import java.util.ArrayList;

public class Playlist {

    public static Playlist instance;
    private final ArrayList<String> arrayList;
    public PlaylistConfig config;

    public Playlist(ArrayList<String> arrayList) {
        this.arrayList = arrayList;
    }

    public static Playlist getInstance(String target){
        if (instance == null){
            instance = new Playlist(loadConfig(target).playlists());
        }
        return instance;
    }

    private static PlaylistConfig loadConfig(String target){
        return new PlaylistConfig(target);
    }


    public ArrayList<String> getArrayList() {
        return arrayList;
    }
}
