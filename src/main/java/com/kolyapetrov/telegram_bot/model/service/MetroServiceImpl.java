package com.kolyapetrov.telegram_bot.model.service;

import com.kolyapetrov.telegram_bot.model.entity.MetroDistance;
import com.kolyapetrov.telegram_bot.model.entity.MetroInfo;
import com.kolyapetrov.telegram_bot.model.repository.MetroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class MetroServiceImpl implements MetroService {
    private final MetroRepository metroRepository;
    private final LocationService locationService;

    @Autowired
    public MetroServiceImpl(MetroRepository metroRepository, LocationService locationService) {
        this.metroRepository = metroRepository;
        this.locationService = locationService;
    }

    @Override
    public List<MetroInfo> findMetroByCity(String city) {
        return metroRepository.findByCity(city);
    }

    @Override
    public List<MetroDistance> findClosestMetroStationByCords(String city, Double orderLongitude, Double orderLatitude) {
        List<MetroInfo> metros = metroRepository.findByCity(city);
        if (metros.isEmpty()) {
            return new ArrayList<>();
        }

        metros.sort(Comparator.comparingDouble(metro -> {
            double dist = locationService.distBetweenPoints(metro.getLatitude(),
                    metro.getLongitude(), orderLatitude, orderLongitude);
            metro.setDistance(dist);
            return dist;
        }));

        List<MetroDistance> result = new ArrayList<>();
        for (int i = 0; i < 3; ++i) {
            result.add(MetroDistance.builder()
                    .metroInfo(metros.get(i))
                    .distance(metros.get(i).getDistance())
                    .build());
        }
        return result;
    }
}
