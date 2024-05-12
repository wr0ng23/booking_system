package com.kolyapetrov.telegram_bot.model.service;

import com.kolyapetrov.telegram_bot.model.entity.MetroDistance;
import com.kolyapetrov.telegram_bot.model.entity.MetroInfo;

import java.util.List;

public interface MetroService {
    List<MetroInfo> findMetroByCity(String city);
    List<MetroDistance> findClosestMetroStationByCords(String city, Double orderLongitude, Double orderLatitude);
}
