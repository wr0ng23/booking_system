package com.kolyapetrov.telegram_bot.model.service;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeoutException;

public interface LocationService {
    String requestInfoAboutLocationByCords(Double latitude, Double longitude);

    Map<String, Double> getCordsByAddress(String address) throws IOException, InterruptedException, TimeoutException;

    double distBetweenPoints(double latitudeA, double longitudeA, double latitudeB, double longitudeB);
}
