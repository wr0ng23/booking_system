package com.kolyapetrov.telegram_bot.model.service;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeoutException;

public interface LocationService {
    void requestInfoAboutLocationByCords(Double latitude, Double longitude);

    Map<String, Double> getCordByAddress(String address) throws IOException, InterruptedException, TimeoutException;
}
