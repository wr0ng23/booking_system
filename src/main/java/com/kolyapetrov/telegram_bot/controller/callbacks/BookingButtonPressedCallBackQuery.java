package com.kolyapetrov.telegram_bot.controller.callbacks;

import com.kolyapetrov.telegram_bot.controller.CallBackHandler;
import com.kolyapetrov.telegram_bot.model.dto.CallBackInfo;
import com.kolyapetrov.telegram_bot.model.dto.UserInfo;
import com.kolyapetrov.telegram_bot.model.service.BookingService;
import com.kolyapetrov.telegram_bot.util.CallBackName;
import com.kolyapetrov.telegram_bot.util.KeyBoardUtil;
import com.kolyapetrov.telegram_bot.util.MessageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class BookingButtonPressedCallBackQuery implements CallBackHandler {
    private final BookingService bookingService;

    @Autowired
    public BookingButtonPressedCallBackQuery(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @Override
    public CallBackName getCallBack() {
        return CallBackName.BOOK_BUTTON;
    }

    @Override
    public void handle(UserInfo userInfo, CallBackInfo callBackInfo, DefaultAbsSender sender) throws TelegramApiException {
        var bookedDates = bookingService.findDatesBetweenStartAndEndForOrder(callBackInfo.getNumberOfOrder());
        sender.execute(MessageUtil.getEditMessageForSeeAds(userInfo.getChatId(), userInfo.getMessageId(),
                KeyBoardUtil.getKeyboardForDates(callBackInfo.getNumberOfOrder(), bookedDates,
                        null, null)));
    }
}
