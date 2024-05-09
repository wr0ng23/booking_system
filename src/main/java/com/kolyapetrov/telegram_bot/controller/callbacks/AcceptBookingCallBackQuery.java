package com.kolyapetrov.telegram_bot.controller.callbacks;

import com.kolyapetrov.telegram_bot.controller.CallBackHandler;
import com.kolyapetrov.telegram_bot.model.dto.CallBackInfo;
import com.kolyapetrov.telegram_bot.model.dto.UserInfo;
import com.kolyapetrov.telegram_bot.model.service.BookingService;
import com.kolyapetrov.telegram_bot.util.BookingTemp;
import com.kolyapetrov.telegram_bot.util.CallBackName;
import com.kolyapetrov.telegram_bot.util.MessageUtil;
import com.kolyapetrov.telegram_bot.util.TempTableManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class AcceptBookingCallBackQuery implements CallBackHandler {
    private final BookingService bookingService;
    private final TempTableManager tempTableManager;


    @Autowired
    public AcceptBookingCallBackQuery(BookingService bookingService, TempTableManager tempTableManager) {
        this.bookingService = bookingService;
        this.tempTableManager = tempTableManager;
    }

    @Override
    public CallBackName getCallBack() {
        return CallBackName.ACCEPT_BOOKING;
    }

    @Override
    public void handle(UserInfo userInfo, CallBackInfo callBackInfo, DefaultAbsSender sender) throws TelegramApiException {
        Long userId = userInfo.getAppUser().getUserId();
        Long orderId = callBackInfo.getNumberOfOrder();

        tempTableManager.createTempTable();
        var tempBookingRecordId = tempTableManager.getRecordId(userId, orderId);
        BookingTemp bookingTemp = tempTableManager.getRecordById(tempBookingRecordId);
        if (bookingTemp == null || bookingTemp.getStartDate() == null || bookingTemp.getEndDate() == null) {
            sender.execute(MessageUtil.getMessage(userInfo.getChatId(),
                    "Вам необходимо выбрать дату начала бронирования и конца!"));
            return;
        }

        tempTableManager.deleteRecordById(tempBookingRecordId);
        bookingService.insertNewRecord(bookingTemp);

        DeleteMessage deleteMessage = new DeleteMessage(userInfo.getChatId(), userInfo.getMessageId());
        sender.execute(deleteMessage);

        //TODO: more details about booking needed
        sender.execute(MessageUtil.getMessage(userInfo.getChatId(), "Объявление забронировано, " +
                "вы можете связаться с арендодателем для уточнения деталей!"));
    }
}
