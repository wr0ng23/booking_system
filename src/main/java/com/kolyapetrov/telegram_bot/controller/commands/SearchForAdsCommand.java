package com.kolyapetrov.telegram_bot.controller.commands;

import com.kolyapetrov.telegram_bot.controller.CommandHandler;
import com.kolyapetrov.telegram_bot.model.entity.AppUser;
import com.kolyapetrov.telegram_bot.model.service.UserService;
import com.kolyapetrov.telegram_bot.util.enums.Command;
import com.kolyapetrov.telegram_bot.util.KeyBoardUtil;
import com.kolyapetrov.telegram_bot.util.MessageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import static com.kolyapetrov.telegram_bot.util.enums.UserState.SEARCH_FOR_ADS;

@Component
public class SearchForAdsCommand implements CommandHandler {
    private final UserService userService;

    @Autowired
    public SearchForAdsCommand(UserService userService) {
        this.userService = userService;
    }

    @Override
    public Command getCommand() {
        return Command.SEARCH_FOR_ADS;
    }

    @Override
    public void handle(Update update, DefaultAbsSender sender) throws TelegramApiException {
        AppUser appUser = userService.getUser(update.getMessage().getFrom());
        String chatId = update.getMessage().getChatId().toString();

        sender.execute(MessageUtil.getMessage(chatId, "Введите название города для поиска объявлений, " +
                        "либо нажмите кнопку ниже для просмотра ближайших мест бронирования!",
                KeyBoardUtil.choiceLocationOrEnterCity()));
        appUser.setUserState(SEARCH_FOR_ADS);
        userService.saveUser(appUser);
    }
}
