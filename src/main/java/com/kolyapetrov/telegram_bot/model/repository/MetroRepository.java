package com.kolyapetrov.telegram_bot.model.repository;

import com.kolyapetrov.telegram_bot.model.entity.MetroInfo;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface MetroRepository extends CrudRepository<MetroInfo, Long> {
    List<MetroInfo> findByCity(String city);
}
