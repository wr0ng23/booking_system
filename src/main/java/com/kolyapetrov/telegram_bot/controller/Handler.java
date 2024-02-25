package com.kolyapetrov.telegram_bot.controller;

import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public interface Handler {
    void handle(Update update, DefaultAbsSender sender) throws TelegramApiException;
}
