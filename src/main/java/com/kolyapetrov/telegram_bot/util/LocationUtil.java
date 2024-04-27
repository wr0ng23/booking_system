package com.kolyapetrov.telegram_bot.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class LocationUtil {
    private LocationUtil() {

    }

    public static double distBetweenPoints(double latitudeA, double longitudeA,
                                           double latitudeB, double longitudeB) {
        final int EARTH_RADIUS = 6372795;
        double lat1 = latitudeA * Math.PI / 180;
        double lat2 = latitudeB * Math.PI / 180;
        double long1 = longitudeA * Math.PI / 180;
        double long2 = longitudeB * Math.PI / 180;

        double cl1 = Math.cos(lat1);
        double cl2 = Math.cos(lat2);
        double sl1 = Math.sin(lat1);
        double sl2 = Math.sin(lat2);
        double delta = long2 - long1;
        double cdelta = Math.cos(delta);
        double sdelta = Math.sin(delta);

        double y = Math.sqrt(Math.pow(cl2 * sdelta, 2) + Math.pow(cl1 * sl2 - sl1 * cl2 * cdelta, 2));
        double x = sl1 * sl2 + cl1 * cl2 * cdelta;

        double ad = Math.atan2(y, x);
        return ad * EARTH_RADIUS;
    }

    public static void requestInfoAboutLocationByCords(Double latitude, Double longitude) {
        String apiUrl = "http://suggestions.dadata.ru/suggestions/api/4_1/rs/geolocate/address";
        String token = "2c7577e9645ac0d3aaa0b5d0e6b97f5aa113de28";

        // Building the JSON payload
        String jsonPayload = "{\"lat\": " + latitude +
                ", \"lon\": " + longitude +
                ", \"count\": 1}";

        // Creating the HTTP client
        HttpClient httpClient = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("Authorization", "Token " + token)
                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("Response Code: " + response.statusCode());
            System.out.println("Response body: " + response.body());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void getCordByAddress(String address) throws IOException, InterruptedException {
//        String query = "Москва, ул. Садовническая, 25";
        String apiKey = "9f8614fb-f5b7-43c1-ad53-de8b831727d4";
        String encodedQuery = URLEncoder.encode(address, StandardCharsets.UTF_8);
        String url = "https://catalog.api.2gis.com/3.0/items/geocode?q=" + encodedQuery +
                "&fields=items.point,items.geometry.centroid&key=" + apiKey;


        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(response.body());

            JsonNode itemsNode = rootNode.path("result").path("items");
            if (itemsNode.isArray() && !itemsNode.isEmpty()) {
                JsonNode item = itemsNode.get(0);
                JsonNode point = item.path("point");
                double lat = point.get("lat").asDouble();
                double lon = point.get("lon").asDouble();

                System.out.println("Широта: " + lat);
                System.out.println("Долгота: " + lon);
            } else {
                System.out.println("Нет данных о координатах для данного запроса.");
            }
        } else {
            System.out.println("Ошибка при выполнении запроса: " + response.statusCode());
        }
    }
}
