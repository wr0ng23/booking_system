package com.kolyapetrov.telegram_bot.model.service;

import java.time.LocalDate;
import java.util.List;

public interface BookingService {
    List<LocalDate> findDatesBetweenStartAndEndForOrder(Long orderId);
}
