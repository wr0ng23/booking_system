package com.kolyapetrov.telegram_bot.controller.callbacks;

import com.kolyapetrov.telegram_bot.controller.CallBackHandler;
import com.kolyapetrov.telegram_bot.model.dto.CallBackInfo;
import com.kolyapetrov.telegram_bot.model.dto.UserInfo;
import com.kolyapetrov.telegram_bot.util.enums.CallBackName;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class AlreadyBookedDatesCallBackQuery implements CallBackHandler {
    @Override
    public CallBackName getCallBack() {
        return CallBackName.ALREADY_BOOKED_DATES;
    }

    @Override
    public void handle(UserInfo userInfo, CallBackInfo callBackInfo, DefaultAbsSender sender) throws TelegramApiException {
        AnswerCallbackQuery answerCallbackQuery = AnswerCallbackQuery.builder()
                .text("Жилье на эту дату уже забронировано!")
                .showAlert(true)
                .callbackQueryId(callBackInfo.getId())
                .build();
        sender.execute(answerCallbackQuery);
    }
}
