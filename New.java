package advisor;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;

public class New {

    public static New instance;
    private final ArrayList<String> arrayList;

    public New(ArrayList<String> arrayList) {
        this.arrayList = arrayList;
    }

    public static New getInstance(){
        if (instance == null){
            instance = new New(newReleases());
        }
        return instance;
    }


    public static ArrayList<String> newReleases() {

        ArrayList<String> arrayList = new ArrayList<>();

        JsonObject responseJson = Api.sendApiRequest("/v1/browse/new-releases");
        try {
            JsonObject albums = responseJson.get("albums").getAsJsonObject();
            for (JsonElement album : albums.getAsJsonArray("items")) {

                String s = "";

                JsonObject albumObject = album.getAsJsonObject();

                s += albumObject.get("name").getAsString()+"\n";

                //artists
                JsonArray artists = albumObject.getAsJsonArray("artists");
                int size = artists.size();
                if (size == 1) {
                    s += "[" + artists.get(0).getAsJsonObject().get("name").getAsString() + "]" +"\n";
                } else if (size > 1) {
                    s += "[";
                    for (int i = 0; i < size; i++) {
                        if (i < (size - 1)) {
                            s += artists.get(i).getAsJsonObject().get("name").getAsString() + ", ";
                        } else {
                            s += artists.get(i).getAsJsonObject().get("name").getAsString() + "]"+"\n";
                        }

                    }
                }
                s += albumObject.get("external_urls").getAsJsonObject().get("spotify").getAsString() + "\n";

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
