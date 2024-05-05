package com.kolyapetrov.telegram_bot.model.service;

import com.kolyapetrov.telegram_bot.util.BookingTemp;

import java.time.LocalDate;
import java.util.List;

public interface BookingService {
    List<LocalDate> findDatesBetweenStartAndEndForOrder(Long orderId);
    void insertNewRecord(BookingTemp bookingTemp);
}
