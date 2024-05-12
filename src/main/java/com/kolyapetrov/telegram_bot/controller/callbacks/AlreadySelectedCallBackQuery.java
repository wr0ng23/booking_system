package com.kolyapetrov.telegram_bot.controller.callbacks;

import com.kolyapetrov.telegram_bot.controller.CallBackHandler;
import com.kolyapetrov.telegram_bot.model.dto.CallBackInfo;
import com.kolyapetrov.telegram_bot.model.dto.UserInfo;
import com.kolyapetrov.telegram_bot.model.service.BookingService;
import com.kolyapetrov.telegram_bot.model.service.BookingTempTableService;
import com.kolyapetrov.telegram_bot.util.*;
import com.kolyapetrov.telegram_bot.model.entity.BookingTemp;
import com.kolyapetrov.telegram_bot.util.enums.CallBackName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDate;

@Component
public class AlreadySelectedCallBackQuery implements CallBackHandler {
    private final BookingService bookingService;
    private final BookingTempTableService bookingTempTableService;

    @Autowired
    public AlreadySelectedCallBackQuery(BookingService bookingService, BookingTempTableService bookingTempTableService) {
        this.bookingService = bookingService;
        this.bookingTempTableService = bookingTempTableService;
    }

    @Override
    public CallBackName getCallBack() {
        return CallBackName.ALREADY_SELECTED_DATES;
    }

    @Override
    public void handle(UserInfo userInfo, CallBackInfo callBackInfo, DefaultAbsSender sender) throws TelegramApiException {
        var bookedDates = bookingService.findBookedDatesByOrderId(callBackInfo.getNumberOfOrder());
        Long userId = userInfo.getAppUser().getUserId();
        Long orderId = callBackInfo.getNumberOfOrder();
        String selectedDate = callBackInfo.getSelectedDate();

        Long recordId = bookingTempTableService.getRecordId(userId, orderId);
        BookingTemp bookingTemp = bookingTempTableService.getRecordById(recordId);

        if (bookingTemp.getEndDate() != null && bookingTemp.getEndDate().equals(LocalDate.parse(selectedDate))) {
            LocalDate startBooking = bookingTempTableService.getStartDateById(recordId);
            bookingTempTableService.updateEndDate(recordId, null);
            sender.execute(MessageUtil.getEditMessageForSeeAds(userInfo.getChatId(), userInfo.getMessageId(),
                    KeyBoardUtil.getKeyboardForDates(callBackInfo.getNumberOfOrder(), bookedDates, startBooking,
                            null)));

        } else if (bookingTemp.getStartDate() != null && bookingTemp.getStartDate().equals(LocalDate.parse(selectedDate))) {
            LocalDate endBooking = bookingTempTableService.getEndDateById(recordId);
            bookingTempTableService.updateStartDate(recordId, null);
            sender.execute(MessageUtil.getEditMessageForSeeAds(userInfo.getChatId(), userInfo.getMessageId(),
                    KeyBoardUtil.getKeyboardForDates(callBackInfo.getNumberOfOrder(), bookedDates, null,
                            endBooking)));

        }
    }
}
