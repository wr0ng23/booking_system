package com.kolyapetrov.telegram_bot.controller.commands;

import com.kolyapetrov.telegram_bot.model.entity.AppUser;
import com.kolyapetrov.telegram_bot.model.entity.UserState;
import com.kolyapetrov.telegram_bot.model.service.UserService;
import com.kolyapetrov.telegram_bot.util.MessageUtil;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

import static com.kolyapetrov.telegram_bot.controller.messages.ConstantMessages.LANDLORD;
import static com.kolyapetrov.telegram_bot.controller.messages.ConstantMessages.TENANT;

@Component
public class StartCommand implements Executable {
    private final UserService userService;
    private static final String DESCRIPTION = "Здесь вы можете сдать в аренду или забронировать жилье.";

    public StartCommand(UserService userService) {
        this.userService = userService;
    }

    @Override
    public SendMessage retrieveMessage(Update update) {
        AppUser appUser = userService.getUser(update.getMessage().getFrom());
        String chatId = update.getMessage().getChatId().toString();

        if (appUser.getUserState() == UserState.REGISTRATION) {
            return MessageUtil.getMessage(chatId, DESCRIPTION, getReplyKeyboardMarkup());
        } else {
            return MessageUtil.getMessage(chatId, "Для смены типа пользователя " +
                    "нажмите другую кнопку!");
        }
    }

    private ReplyKeyboardMarkup getReplyKeyboardMarkup() {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(true);
        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        row.add(TENANT);
        row.add(LANDLORD);
        keyboard.add(row);
        keyboardMarkup.setKeyboard(keyboard);
        return keyboardMarkup;
    }
}
