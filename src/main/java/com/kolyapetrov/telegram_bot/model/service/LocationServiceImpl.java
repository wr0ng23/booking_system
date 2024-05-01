package com.kolyapetrov.telegram_bot.model.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

@Service
public class LocationServiceImpl implements LocationService {
    @Value("${2gis.token}")
    private String token2Gis;

    @Value("${daData.token}")
    private String tokenDaData;

    @Override
    public double distBetweenPoints(double latitudeA, double longitudeA, double latitudeB, double longitudeB) {
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

    @Override
    public String requestInfoAboutLocationByCords(Double latitude, Double longitude) {
        String apiUrl = "http://suggestions.dadata.ru/suggestions/api/4_1/rs/geolocate/address";

        String jsonPayload = "{\"lat\": " + latitude +
                ", \"lon\": " + longitude +
                ", \"count\": 1}";

        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("Authorization", "Token " + tokenDaData)
                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(response.body());
            return rootNode.get("suggestions").get(0).get("data").get("city").asText();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    public Map<String, Double> getCordsByAddress(String address) throws IOException, InterruptedException, TimeoutException {
        String encodedQuery = URLEncoder.encode(address, StandardCharsets.UTF_8);
        String url = "https://catalog.api.2gis.com/3.0/items/geocode?q=" + encodedQuery +
                "&fields=items.point,items.geometry.centroid&key=" + token2Gis;

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

                Map<String, Double> latAndLon = new HashMap<>();
                latAndLon.put("lat", lat);
                latAndLon.put("lon", lon);
                return latAndLon;
            } else {
                throw new TimeoutException("Empty json response when location request was done");
            }
        } else {
            throw new TimeoutException("Error in location service when requset was done, error code: " +
                    response.statusCode());
        }
    }
}
