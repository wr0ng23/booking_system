package com.kolyapetrov.telegram_bot.controller.commands;

import com.kolyapetrov.telegram_bot.controller.CommandHandler;
import com.kolyapetrov.telegram_bot.model.entity.AppUser;
import com.kolyapetrov.telegram_bot.model.service.UserService;
import com.kolyapetrov.telegram_bot.util.Command;
import com.kolyapetrov.telegram_bot.util.KeyBoardUtil;
import com.kolyapetrov.telegram_bot.util.MessageUtil;
import com.kolyapetrov.telegram_bot.util.UserState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class StartCommand implements CommandHandler {
    private final UserService userService;

    private static final String DESCRIPTION = "Здесь вы можете сдать в аренду или забронировать жилье!";

    @Autowired
    public StartCommand(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void handle(Update update, DefaultAbsSender sender) throws TelegramApiException {
        String chatId = update.getMessage().getChatId().toString();
        AppUser appUser = userService.getUser(update.getMessage().getFrom());
        appUser.setUserState(UserState.MAIN);
        userService.saveUser(appUser);

        sender.execute(MessageUtil.getMessage(chatId, DESCRIPTION, KeyBoardUtil.mainKeyBoard()));
    }

    @Override
    public Command getCommand() {
        return Command.START;
    }
}
