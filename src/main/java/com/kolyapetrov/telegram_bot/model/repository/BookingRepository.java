package com.kolyapetrov.telegram_bot.model.repository;

import com.kolyapetrov.telegram_bot.model.entity.Booking;
import com.kolyapetrov.telegram_bot.model.entity.Order;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface BookingRepository extends CrudRepository<Booking, Long> {
    List<Booking> findByOrder(Order order);
    List<Booking> findByOrder_Id(Long orderId);
}
