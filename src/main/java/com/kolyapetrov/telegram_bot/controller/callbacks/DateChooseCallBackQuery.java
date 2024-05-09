package com.kolyapetrov.telegram_bot.controller.callbacks;

import com.kolyapetrov.telegram_bot.controller.CallBackHandler;
import com.kolyapetrov.telegram_bot.model.dto.CallBackInfo;
import com.kolyapetrov.telegram_bot.model.dto.UserInfo;
import com.kolyapetrov.telegram_bot.model.service.BookingService;
import com.kolyapetrov.telegram_bot.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Component
public class DateChooseCallBackQuery implements CallBackHandler {
    private final BookingService bookingService;
    private final TempTableManager tempTableManager;

    @Autowired
    public DateChooseCallBackQuery(BookingService bookingService, TempTableManager tempTableManager) {
        this.bookingService = bookingService;
        this.tempTableManager = tempTableManager;
    }

    @Override
    public CallBackName getCallBack() {
        return CallBackName.SELECT_DATE;
    }

    @Override
    public void handle(UserInfo userInfo, CallBackInfo callBackInfo, DefaultAbsSender sender) throws TelegramApiException {
        var bookedDates = bookingService.findDatesBetweenStartAndEndForOrder(callBackInfo.getNumberOfOrder());
        Long userId = userInfo.getAppUser().getUserId();
        Long orderId = callBackInfo.getNumberOfOrder();
        String selectedDate = callBackInfo.getSelectedDate();

        tempTableManager.createTempTable();
        Long recordId = tempTableManager.getRecordId(userId, orderId);
        BookingTemp bookingTemp = tempTableManager.getRecordById(recordId);

        LocalDate startBooking = null, endBooking = null;
        if (bookingTemp != null) {
            startBooking = bookingTemp.getStartDate();
            endBooking = bookingTemp.getEndDate();
        }
        if (!checkDatesCorrectness(startBooking, endBooking, LocalDate.parse(selectedDate)) ||
                !checkPeriod(startBooking, endBooking, LocalDate.parse(selectedDate), bookedDates)) {
            sender.execute(MessageUtil.getMessage(userInfo.getChatId(), "Вы не можете выбрать такие даты!"));
            return;
        }

        if (bookingTemp == null || startBooking == null) {
            if (bookingTemp == null) {
                tempTableManager.insertDate(userId, callBackInfo.getNumberOfOrder(), selectedDate);
            } else {
                tempTableManager.updateStartDate(recordId, selectedDate);
            }
            sender.execute(MessageUtil.getEditMessageForSeeAds(userInfo.getChatId(), userInfo.getMessageId(),
                    KeyBoardUtil.getKeyboardForDates(callBackInfo.getNumberOfOrder(), bookedDates,
                            LocalDate.parse(selectedDate), endBooking)));

        } else if (endBooking == null) {
            tempTableManager.updateEndDate(recordId, selectedDate);
            sender.execute(MessageUtil.getEditMessageForSeeAds(userInfo.getChatId(), userInfo.getMessageId(),
                    KeyBoardUtil.getKeyboardForDates(callBackInfo.getNumberOfOrder(), bookedDates, startBooking,
                            LocalDate.parse(selectedDate))));
        } else {
            sender.execute(MessageUtil.getMessage(userInfo.getChatId(), "Для того чтобы выбрать новые даты, " +
                    "уберите флажки со старых!"));
        }
    }

    private List<LocalDate> getFreeDates(List<LocalDate> bookedDates) {
        LocalDate currentDate = LocalDate.now();
        int daysInMonth = currentDate.lengthOfMonth();
        List<LocalDate> freeDates = new ArrayList<>();

        for (int day = currentDate.getDayOfMonth(); day <= daysInMonth; day++) {
            LocalDate date = currentDate.withDayOfMonth(day);
            if (!bookedDates.contains(date)) {
                freeDates.add(date);
            }
        }
        return freeDates;
    }

    private boolean checkDatesCorrectness(LocalDate startDate, LocalDate endDate, LocalDate selectedDate) {
        if (startDate == null && endDate == null) return true;
        if (startDate == null) {
            return !selectedDate.isAfter(endDate);
        } else if (endDate == null) {
            return !selectedDate.isBefore(startDate);
        } else return true;
    }

    private boolean checkPeriod(LocalDate startDate, LocalDate endDate, LocalDate selectedDate, List<LocalDate> bookedDates) {
        if (startDate == null && endDate == null) return true;
        if (startDate == null) {
            startDate = selectedDate;
        } else if (endDate == null) {
            endDate = selectedDate;
        }
        var list = getFreeDates(bookedDates);
        var days = startDate.datesUntil(endDate).toList();
        return new HashSet<>(list).containsAll(days);
    }
}
