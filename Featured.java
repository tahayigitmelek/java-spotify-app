package advisor;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;

public class Featured {

    public static Featured instance;
    private final ArrayList<String> arrayList;


    public Featured(ArrayList<String> arrayList) {
        this.arrayList = arrayList;
    }

    public static Featured getInstance(){
        if (instance == null){
            instance = new Featured(featuredReleases());
        }
        return instance;
    }

    public static ArrayList<String> featuredReleases() {
        ArrayList<String> arrayList = new ArrayList<>();
        JsonObject responseJson = Api.sendApiRequest("/v1/browse/featured-playlists");

        try {
            JsonObject playlists = responseJson.get("playlists").getAsJsonObject();
            for (JsonElement playlist : playlists.getAsJsonArray("items")) {
                String s = "";
                JsonObject playlistObject = playlist.getAsJsonObject();

                s += playlistObject.get("name").getAsString()+"\n";
                s += playlistObject.get("external_urls").getAsJsonObject().get("spotify").getAsString() + "\n";

                arrayList.add(s);
            }
        } catch (Exception e) {
            System.out.println("Error, please try again");
        }
        return arrayList;
    }

    public ArrayList<String> getArrayList() {
        return arrayList;
    }
}
