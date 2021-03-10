package advisor;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;

public class Categories {

    public static Categories instance;
    private final ArrayList<String> arrayList;

    public Categories(ArrayList<String> arrayList) {
        this.arrayList = arrayList;
    }

    public static Categories getInstance(){
        if (instance == null){
            instance = new Categories(categories());
        }
        return instance;
    }


    public static ArrayList<String> categories() {
        ArrayList<String> arrayList = new ArrayList<>();
        JsonObject responseJson = Api.sendApiRequest("/v1/browse/categories");
        try {
            JsonObject categories = responseJson.get("categories").getAsJsonObject();
            for (JsonElement category : categories.getAsJsonArray("items")) {
                JsonObject categoryObject = category.getAsJsonObject();
                arrayList.add(categoryObject.get("name").getAsString());
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
