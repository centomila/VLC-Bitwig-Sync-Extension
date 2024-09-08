package com.centomila;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;

public class VLCController {

    private static final String VLC_URL = "http://localhost:8080/requests/status.xml";
    private static final String USERNAME = "";
    private static final String PASSWORD = "test";

    private static final HttpClient httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .build();

    public static void sendCommand(String command) throws Exception {
        URI uri = URI.create(VLC_URL + "?command=" + command);

        String auth = USERNAME + ":" + PASSWORD;
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .header("Authorization", "Basic " + encodedAuth)
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println("Response Code: " + response.statusCode());
        System.out.println("Response Body: " + response.body());
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Please provide a command (e.g., pl_play, pl_stop).");
            return;
        }

        String command = args[0];

        try {
            sendCommand(command);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
