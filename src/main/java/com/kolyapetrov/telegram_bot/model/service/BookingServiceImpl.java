package com.kolyapetrov.telegram_bot.model.service;

import com.kolyapetrov.telegram_bot.model.entity.Booking;
import com.kolyapetrov.telegram_bot.model.repository.BookingRepository;
import com.kolyapetrov.telegram_bot.model.repository.OrderRepository;
import com.kolyapetrov.telegram_bot.model.repository.UserRepository;
import com.kolyapetrov.telegram_bot.model.entity.BookingTemp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

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
    public List<LocalDate> findBookedDatesByOrderId(Long orderId) {
        List<Booking> bookings = bookingRepository.findByOrder_Id(orderId);

        Set<LocalDate> bookedDates = new HashSet<>();
//        Map<LocalDate, Integer> bookedDatesMap = new HashMap<>();

        for (Booking booking : bookings) {
            LocalDate startDate = booking.getDateStart();
            LocalDate endDate = booking.getDateEnd();
            if (endDate.isBefore(LocalDate.now())) continue;
            LocalDate currentDate = startDate;
            while (!currentDate.isAfter(endDate)) {
                bookedDates.add(currentDate);
                currentDate = currentDate.plusDays(1);
            }

            /*startDate = booking.getDateStart();
            endDate = booking.getDateEnd();
            if (endDate.isBefore(LocalDate.now())) continue;
            currentDate = startDate;
            while (!currentDate.isAfter(endDate)) {
                if (bookedDatesMap.containsKey(currentDate)) {
                    bookedDatesMap.put(currentDate, bookedDatesMap.get(currentDate) + 1);
                } else {
                    bookedDatesMap.put(currentDate, 1);
                }
                currentDate = currentDate.plusDays(1);
            }*/
        }

        /*var someDates = bookedDatesMap.entrySet().stream()
                .filter(predicate -> predicate.getValue().equals(2) || predicate.getKey().equals(LocalDate.now()))
                .map(Map.Entry::getKey).toList();
        bookedDates.addAll(someDates);*/

        return bookedDates.stream().toList();
    }

    @Override
    public List<LocalDate> findFreeDatesForBookingByOrderId(Long orderId) {
        var bookedDates = findBookedDatesByOrderId(orderId);

        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.of(LocalDate.now().getYear(), LocalDate.now().getMonth(), 1)
                .plusMonths(1)
                .minusDays(1);

        List<LocalDate> currentLeftDaysInMonth = new ArrayList<>();
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            currentLeftDaysInMonth.add(date);
        }
        currentLeftDaysInMonth.removeAll(bookedDates);

        return currentLeftDaysInMonth;
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
