package com.kolyapetrov.telegram_bot.controller.callbacks;

import com.kolyapetrov.telegram_bot.controller.CallBackHandler;
import com.kolyapetrov.telegram_bot.model.dto.CallBackInfo;
import com.kolyapetrov.telegram_bot.model.dto.UserInfo;
import com.kolyapetrov.telegram_bot.model.service.BookingService;
import com.kolyapetrov.telegram_bot.util.CallBackName;
import com.kolyapetrov.telegram_bot.util.KeyBoardUtil;
import com.kolyapetrov.telegram_bot.util.MessageUtil;
import com.kolyapetrov.telegram_bot.util.TempTableManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDate;

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
        Long tempTableRecordId = tempTableManager.getRecordId(userId, orderId);

        if (tempTableRecordId == null) {
            tempTableManager.insertDate(userId, callBackInfo.getNumberOfOrder(), selectedDate);
            sender.execute(MessageUtil.getEditMessageForSeeAds(userInfo.getChatId(), userInfo.getMessageId(),
                    KeyBoardUtil.getKeyboardForDates(callBackInfo.getNumberOfOrder(), bookedDates,
                            LocalDate.parse(selectedDate), null)));
        } else {
            tempTableManager.updateEndDate(tempTableRecordId, callBackInfo.getSelectedDate());
            LocalDate startDate = tempTableManager.getStartDateById(tempTableRecordId);
            sender.execute(MessageUtil.getEditMessageForSeeAds(userInfo.getChatId(), userInfo.getMessageId(),
                    KeyBoardUtil.getKeyboardForDates(callBackInfo.getNumberOfOrder(), bookedDates, startDate,
                            LocalDate.parse(selectedDate))));
        }
    }
}
