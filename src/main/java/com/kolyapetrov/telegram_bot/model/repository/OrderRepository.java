package com.kolyapetrov.telegram_bot.model.repository;

import com.kolyapetrov.telegram_bot.model.entity.Order;
import org.springframework.data.repository.CrudRepository;

public interface OrderRepository extends CrudRepository<Order, Long> {

}
