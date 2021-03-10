package advisor;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Scanner;

public class Main {

    public static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) throws IOException, InterruptedException {
        if (args.length > 0) {
            for (int i = 0; i < args.length - 1; i++) {
                if (args[i] == "-access") {
                    if (args[i + 1].length() > 0) {
                        Config.AUTH_SERVER_PATH = args[i + 1];
                    }
                } else if (args[i] == "-resource") {
                    if (args[i + 1].length() > 0) {
                        Config.API_SERVER_PATH = args[i + 1];
                    }
                }else if (args[i] == "-page") {
                    if (args[i + 1].length() > 0) {
                        Config.PAGE = args[i + 1];
                    }
                }
            }
        }
        initialInput();
    }

    static void initialInput() throws IOException, InterruptedException {
        String input = scanner.nextLine();

        switch (input) {
            case "auth":
                authorize();
                break;
            case "exit":
                System.out.println("---GOODBYE!---");
                break;
            default:
                System.out.println("Please, provide access for application.");
                initialInput();
                break;
        }
    }

    static void authorize() throws IOException, InterruptedException {
        System.out.println(Config.AUTH_SERVER_PATH + "/authorize?" +
                "client_id=" + Config.CLIENT_ID + "&redirect_uri=" + Config.REDIRECT_URI + "&response_type=code");
        System.out.println("waiting for code...");
        HttpServer server = HttpServer.create();
        server.bind(new InetSocketAddress(8080), 0);
        server.start();
        server.createContext("/",
                new HttpHandler() {
                    @Override
                    public void handle(HttpExchange exchange) throws IOException {
                        String str;
                        String query = exchange.getRequestURI().getQuery();
                        if (query != null) {
                            if (query.startsWith("code")) {
                                str = "Got the code. Return back to your program.";
                                Config.AUTH_CODE = query;
                            } else {
                                str = "Authorization code not found. Try again.";
                            }
                        } else {
                            str = "Authorization code not found. Try again.";
                        }

                        exchange.sendResponseHeaders(200, str.length());
                        exchange.getResponseBody().write(str.getBytes());
                        exchange.getResponseBody().close();
                    }
                }
        );
        while (Config.AUTH_CODE.isBlank()) {
            Thread.sleep(10);
        }
        server.stop(10);
        getToken();

    }

    static void getToken() throws IOException, InterruptedException {
        System.out.println("code received");
        System.out.println("making http request for access_token...");
        String encodedKey = Base64.getEncoder().encodeToString((Config.CLIENT_ID + ":" + Config.CLIENT_SECRET).getBytes());
        var client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("Authorization", "Basic " + encodedKey)
                .uri(URI.create(Config.AUTH_SERVER_PATH + "/api/token"))
                .POST(HttpRequest.BodyPublishers.ofString("grant_type=authorization_code&"
                        + Config.AUTH_CODE +
                        "&redirect_uri=" + Config.REDIRECT_URI))
                .build();
        HttpResponse<String> response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }

        JsonObject responseJson = JsonParser.parseString(String.valueOf(response.body())).getAsJsonObject();
        try {
            Config.ACCESS_TOKEN = responseJson.get("access_token").getAsString();
            System.out.println("---SUCCESS---");
            takeInput();
        } catch (Exception e) {
            System.out.println("Failed, please re-authorize.");
            initialInput();
        }

    }

    static void takeInput() throws InterruptedException {

        String input = scanner.next();

        switch (input) {
            case "new":
                New newRelease = New.getInstance();
                getInput(newRelease.getArrayList());
                takeInput();
                break;

            case "featured":
                Featured featured = Featured.getInstance();
                getInput(featured.getArrayList());
                takeInput();
                break;

            case "categories":
                Categories categories = Categories.getInstance();
                getInput(categories.getArrayList());
                takeInput();
                break;

            case "playlists":
                String target = scanner.next().trim();
                Playlist playlist = Playlist.getInstance(target);
                getInput(playlist.getArrayList());
                takeInput();
                break;

            case "exit":
                System.out.println("---GOODBYE!---");
                break;

            default:
                System.out.println("Input not recognized, please try again");
                takeInput();
                break;
        }

    }

    static void getInput(ArrayList<String> arrayList){
        int n = 1;
        int page = Integer.parseInt(Config.PAGE);
        for (int i = 0; i< arrayList.size(); i++){
            if (i == 5) break;
            System.out.println(arrayList.get(i));
        }
        System.out.printf("---PAGE %d OF %d---\n",n,page);
        while (true){
            String input = scanner.next();
            if (input.equals("prev")){
                n--;
                if (n==0){
                    System.out.println("No more pages.");
                }else {
                    for (int i = ((n-1)*5); i<page*n; i++){
                        if (i == 5) break;
                        System.out.println(arrayList.get(i));
                    }
                    System.out.printf("---PAGE %d OF %d---\n",n,page);
                }
            }else if (input.equals("next")){
                n++;
                if (n > page){
                    System.out.println("No more pages.");
                }else {
                    for (int i = ((n-1)*5); i<page*n; i++){
                        if (i == 5) break;
                        System.out.println(arrayList.get(i));
                    }
                    System.out.printf("---PAGE %d OF %d---\n",n,page);
                }
            }else {
                break;
            }
        }
    }
}