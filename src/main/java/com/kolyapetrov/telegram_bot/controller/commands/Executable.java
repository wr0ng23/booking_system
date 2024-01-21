package com.kolyapetrov.telegram_bot.controller.commands;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

//TODO: maybe make /exit command for clear all data about user
public interface Executable {
    SendMessage retrieveMessage(Update update);
}
