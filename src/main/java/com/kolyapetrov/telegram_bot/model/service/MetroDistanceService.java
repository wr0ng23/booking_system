package com.kolyapetrov.telegram_bot.model.service;

import com.kolyapetrov.telegram_bot.model.entity.MetroDistance;

import java.util.List;

public interface MetroDistanceService {
    void saveMetroDistances(List<MetroDistance> distanceList);
}
