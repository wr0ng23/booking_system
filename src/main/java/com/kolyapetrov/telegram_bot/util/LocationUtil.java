package com.kolyapetrov.telegram_bot.util;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class LocationUtil {
    private double distBetweenPoints(double latitudeA, double longitudeA, double latitudeB, double longitudeB) {
        final int EARTH_RADIUS = 6372795;
        double lat1 = latitudeA * Math.PI / 180;
        double lat2 = latitudeB * Math.PI / 180;
        double long1 = longitudeA * Math.PI / 180;
        double long2 = longitudeB * Math.PI / 180;

        // косинусы и синусы широт и разницы долгот
        double cl1 = Math.cos(lat1);
        double cl2 = Math.cos(lat2);
        double sl1 = Math.sin(lat1);
        double sl2 = Math.sin(lat2);
        double delta = long2 - long1;
        double cdelta = Math.cos(delta);
        double sdelta = Math.sin(delta);

        // вычисления длины большого круга
        double y = Math.sqrt(Math.pow(cl2 * sdelta, 2) + Math.pow(cl1 * sl2 - sl1 * cl2 * cdelta, 2));
        double x = sl1 * sl2 + cl1 * cl2 * cdelta;

        double ad = Math.atan2(y, x);
        return ad * EARTH_RADIUS;
    }

    private void requestInfoAboutLocationByCords(Double latitude, Double longitude) {
        String apiUrl = "http://suggestions.dadata.ru/suggestions/api/4_1/rs/geolocate/address";
        String token = "2c7577e9645ac0d3aaa0b5d0e6b97f5aa113de28";

        // Building the JSON payload
        String jsonPayload = "{\"lat\": " + latitude +
                ", \"lon\": " + longitude +
                ", \"count\": 1}";

        // Creating the HTTP client
        HttpClient httpClient = HttpClient.newHttpClient();

        // Creating the HTTP request
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("Authorization", "Token " + token)
                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                .build();

        try {
            // Sending the HTTP request and receiving the response
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            // Displaying the response status code and body
            System.out.println("Response Code: " + response.statusCode());
            System.out.println("Response body: " + response.body());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
