package com.centomila;

import java.io.IOException;
import java.util.Base64;

public class VLCController {

    private static final String VLC_URL = "http://localhost:8080/requests/status.xml";
    private static final String USERNAME = "";
    private static final String PASSWORD = "test";

    public void sendCommand(String command) throws IOException, InterruptedException {
        
        String url = VLC_URL + "?command=" + command;

        String response = HttpClientWrapper.sendGetRequest(url, USERNAME, PASSWORD);

        System.out.println("Response: " + response);
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Please provide a command (e.g., pl_play, pl_stop).");
            return;
        }

        StringBuilder command = new StringBuilder();
        for (String arg : args) {
            command.append(arg).append(" ");
        }

        VLCController controller = new VLCController();
        try {
            System.out.println("Sending command: " + command);
            controller.sendCommand(command.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

// HttpClientWrapper class (as defined in the previous message)
class HttpClientWrapper {
    private static final boolean USE_JAVA11_HTTP_CLIENT;

    static {
        boolean java11HttpClientAvailable = false;
        try {
            Class.forName("java.net.http.HttpClient");
            java11HttpClientAvailable = true;
        } catch (ClassNotFoundException e) {
            // Java 11 HttpClient is not available
        }
        USE_JAVA11_HTTP_CLIENT = java11HttpClientAvailable;
    }

    public static String sendGetRequest(String url, String username, String password)
            throws IOException, InterruptedException {
        if (USE_JAVA11_HTTP_CLIENT) {
            return sendGetRequestJava11(url, username, password);
        } else {
            return sendGetRequestApache(url, username, password);
        }
    }

    private static String sendGetRequestJava11(String url, String username, String password)
            throws IOException, InterruptedException {
        java.net.http.HttpClient client = java.net.http.HttpClient.newHttpClient();
        String encodedAuth = Base64.getEncoder().encodeToString((username + ":" + password).getBytes());

        java.net.http.HttpRequest request = java.net.http.HttpRequest.newBuilder()
                .uri(java.net.URI.create(url))
                .header("Authorization", "Basic " + encodedAuth)
                .build();

        java.net.http.HttpResponse<String> response = client.send(request,
                java.net.http.HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    private static String sendGetRequestApache(String url, String username, String password) throws IOException {
        org.apache.http.client.HttpClient client = org.apache.http.impl.client.HttpClientBuilder.create().build();
        org.apache.http.client.methods.HttpGet request = new org.apache.http.client.methods.HttpGet(url);

        String encodedAuth = Base64.getEncoder().encodeToString((username + ":" + password).getBytes());
        request.setHeader("Authorization", "Basic " + encodedAuth);

        org.apache.http.HttpResponse response = client.execute(request);
        return org.apache.http.util.EntityUtils.toString(response.getEntity());
    }
}