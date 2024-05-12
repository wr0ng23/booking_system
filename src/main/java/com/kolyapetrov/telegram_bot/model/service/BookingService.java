package com.kolyapetrov.telegram_bot.model.service;

import com.kolyapetrov.telegram_bot.model.entity.Booking;
import com.kolyapetrov.telegram_bot.model.entity.BookingTemp;

import java.time.LocalDate;
import java.util.List;

public interface BookingService {
    List<LocalDate> findFreeDatesForBookingByOrderId(Long orderId);
    Booking insertNewRecord(BookingTemp bookingTemp);
    List<LocalDate> findBookedDatesByOrderId(Long orderId);
    void deleteBookingById(Long bookingId);
    void saveBooking(Booking booking);
    Booking findBookingById(Long bookingId);
    Booking findBooking(BookingTemp bookingTemp);
}
