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
public class BookingButtonPressedCallBackQuery implements CallBackHandler {
    private final BookingService bookingService;
    private final TempTableManager tempTableManager;

    @Autowired
    public BookingButtonPressedCallBackQuery(BookingService bookingService, TempTableManager tempTableManager) {
        this.bookingService = bookingService;
        this.tempTableManager = tempTableManager;
    }

    @Override
    public CallBackName getCallBack() {
        return CallBackName.BOOK_BUTTON;
    }

    @Override
    public void handle(UserInfo userInfo, CallBackInfo callBackInfo, DefaultAbsSender sender) throws TelegramApiException {
        tempTableManager.createTempTable();
        Long recordId = tempTableManager.getRecordId(userInfo.getAppUser().getUserId(), callBackInfo.getNumberOfOrder());
        var bookingRecord = tempTableManager.getRecordById(recordId);
        LocalDate startBooking = null, endBooking= null;
        if (bookingRecord != null) {
            startBooking = bookingRecord.getStartDate();
            endBooking = bookingRecord.getEndDate();
        }

        var bookedDates = bookingService.findBookedDatesByOrderId(callBackInfo.getNumberOfOrder());
        sender.execute(MessageUtil.getEditMessageForSeeAds(userInfo.getChatId(), userInfo.getMessageId(),
                KeyBoardUtil.getKeyboardForDates(callBackInfo.getNumberOfOrder(), bookedDates,
                        startBooking, endBooking)));
    }
}
