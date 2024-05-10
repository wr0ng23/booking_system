package com.kolyapetrov.telegram_bot.controller;

import com.kolyapetrov.telegram_bot.model.dto.CallBackInfo;
import com.kolyapetrov.telegram_bot.model.dto.UserInfo;
import com.kolyapetrov.telegram_bot.util.enums.CallBackName;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public interface CallBackHandler {
    CallBackName getCallBack();
    void handle(UserInfo userInfo, CallBackInfo callBackInfo, DefaultAbsSender sender)
            throws TelegramApiException;
}
