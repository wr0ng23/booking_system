package com.kolyapetrov.telegram_bot.model.service;

import com.kolyapetrov.telegram_bot.model.entity.MetroDistance;
import com.kolyapetrov.telegram_bot.model.repository.MetroDistanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MetroDistanceServiceImpl implements MetroDistanceService{
    private final MetroDistanceRepository metroDistanceRepository;

    @Autowired
    public MetroDistanceServiceImpl(MetroDistanceRepository metroDistanceRepository) {
        this.metroDistanceRepository = metroDistanceRepository;
    }

    @Override
    public void saveMetroDistances(List<MetroDistance> distanceList) {
        metroDistanceRepository.saveAll(distanceList);
    }
}

