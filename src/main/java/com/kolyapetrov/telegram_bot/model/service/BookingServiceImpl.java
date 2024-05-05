package com.kolyapetrov.telegram_bot.model.service;

import com.kolyapetrov.telegram_bot.model.entity.Booking;
import com.kolyapetrov.telegram_bot.model.repository.BookingRepository;
import com.kolyapetrov.telegram_bot.model.repository.OrderRepository;
import com.kolyapetrov.telegram_bot.model.repository.UserRepository;
import com.kolyapetrov.telegram_bot.util.BookingTemp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    @Autowired
    public BookingServiceImpl(BookingRepository bookingRepository, OrderRepository orderRepository,
                              UserRepository userRepository) {
        this.bookingRepository = bookingRepository;
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<LocalDate> findDatesBetweenStartAndEndForOrder(Long orderId) {
        List<Booking> bookings = bookingRepository.findByOrder_Id(orderId);
        if (bookings.isEmpty()) {
            return new ArrayList<>();
        }

        List<LocalDate> dates = new ArrayList<>();
        for (Booking booking : bookings) {
            LocalDate startDate = booking.getDateStart();
            LocalDate endDate = booking.getDateEnd();
            LocalDate currentDate = startDate;
            while (!currentDate.isAfter(endDate)) {
                dates.add(currentDate);
                currentDate = currentDate.plusDays(1);
            }
        }

        return dates;
    }

    @Override
    public void insertNewRecord(BookingTemp bookingTemp) {
        Booking booking = new Booking();
        booking.setDateStart(bookingTemp.getStartDate());
        booking.setDateEnd(bookingTemp.getEndDate());
        booking.setUser(userRepository.findById(bookingTemp.getUserId()).get());
        booking.setOrder(orderRepository.findById(bookingTemp.getOrderId()).get());

        bookingRepository.save(booking);
    }
}
