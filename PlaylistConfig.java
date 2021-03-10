package advisor;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;

public class PlaylistConfig {

    public String target;

    public PlaylistConfig(String target) {
        this.target = target;
    }

    public ArrayList<String> playlists() {
        String categoryId = "";
        ArrayList<String> arrayList = new ArrayList<>();
        categoryId = findCategoryId(this.target);
        if (categoryId.equals("")) {
            System.out.println("Unknown category name.");
        } else {
            try {
                String s = "";
                System.out.println(categoryId);
                JsonObject responseJson = Api.sendApiRequest("/v1/browse/categories/" + categoryId + "/playlists");
                if (responseJson.get("error") != null) {
                    System.out.println(responseJson.get("error").getAsJsonObject().get("message").getAsString());
                } else {
                    JsonObject playlists = responseJson.get("playlists").getAsJsonObject();
                    for (JsonElement playlist : playlists.getAsJsonArray("items")) {
                        JsonObject playlistObject = playlist.getAsJsonObject();

                        s += playlistObject.get("name").getAsString()+"\n";
                        s += playlistObject.get("external_urls").getAsJsonObject().get("spotify").getAsString() + "\n";
                        arrayList.add(s);
                    }
                }
            } catch (Exception e) {
                System.out.println("Error, please try again");
            }
            return arrayList;
        }
        return null;
    }

    public static String findCategoryId(String target) {
        String path = "/v1/browse/categories";
        String categoryId = "";

        try {
            while (categoryId.equals("")) {
                JsonObject responseJson = Api.sendApiRequest(path);
                JsonObject categories = responseJson.get("categories").getAsJsonObject();
                for (JsonElement category : categories.getAsJsonArray("items")) {
                    JsonObject categoryObject = category.getAsJsonObject();
                    if (categoryObject.get("name").getAsString().equals(target)) {
                        categoryId = categoryObject.get("id").getAsString();
                    }
                }
                JsonElement pathElement = responseJson.get("next");
                if (pathElement != null) {
                    path = pathElement.getAsString();
                }
                else {
                    break;
                }
            }

        } catch (Exception e) {
            System.out.println("Error, please try again");
        }
        return categoryId;
    }
}
