package com.kolyapetrov.telegram_bot.controller.commands;

import com.kolyapetrov.telegram_bot.util.MessageUtil;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class UnknownCommand implements Executable {
    public static final String UNKNOWN_MESSAGE = "Неизвестная мне команда!";

    @Override
    public SendMessage retrieveMessage(Update update) {
        return MessageUtil.getMessage(update.getMessage().getChatId().toString(),
                UNKNOWN_MESSAGE);
    }
}
