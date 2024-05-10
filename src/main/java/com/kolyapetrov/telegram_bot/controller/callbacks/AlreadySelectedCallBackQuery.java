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

@Component
public class AlreadySelectedCallBackQuery implements CallBackHandler {
    private final BookingService bookingService;
    private final TempTableManager tempTableManager;

    @Autowired
    public AlreadySelectedCallBackQuery(BookingService bookingService, TempTableManager tempTableManager) {
        this.bookingService = bookingService;
        this.tempTableManager = tempTableManager;
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

        Long recordId = tempTableManager.getRecordId(userId, orderId);
        BookingTemp bookingTemp = tempTableManager.getRecordById(recordId);

        if (bookingTemp.getEndDate() != null && bookingTemp.getEndDate().equals(LocalDate.parse(selectedDate))) {
            LocalDate startBooking = tempTableManager.getStartDateById(recordId);
            tempTableManager.updateEndDate(recordId, null);
            sender.execute(MessageUtil.getEditMessageForSeeAds(userInfo.getChatId(), userInfo.getMessageId(),
                    KeyBoardUtil.getKeyboardForDates(callBackInfo.getNumberOfOrder(), bookedDates, startBooking,
                            null)));

        } else if (bookingTemp.getStartDate() != null && bookingTemp.getStartDate().equals(LocalDate.parse(selectedDate))) {
            LocalDate endBooking = tempTableManager.getEndDateById(recordId);
            tempTableManager.updateStartDate(recordId, null);
            sender.execute(MessageUtil.getEditMessageForSeeAds(userInfo.getChatId(), userInfo.getMessageId(),
                    KeyBoardUtil.getKeyboardForDates(callBackInfo.getNumberOfOrder(), bookedDates, null,
                            endBooking)));

        }
    }
}
