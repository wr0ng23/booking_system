package com.kolyapetrov.telegram_bot.model.service;

import com.kolyapetrov.telegram_bot.util.enums.BookingTemp;

import java.time.LocalDate;
import java.util.List;

public interface BookingService {
    List<LocalDate> findFreeDatesForBookingByOrderId(Long orderId);
    void insertNewRecord(BookingTemp bookingTemp);
    List<LocalDate> findBookedDatesByOrderId(Long orderId);
}
