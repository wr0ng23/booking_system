package com.kolyapetrov.telegram_bot.controller.actions;

import com.kolyapetrov.telegram_bot.controller.ActionHandler;
import com.kolyapetrov.telegram_bot.controller.commands.SearchForAdsCommand;
import com.kolyapetrov.telegram_bot.model.entity.AppUser;
import com.kolyapetrov.telegram_bot.model.service.SearchAdsFilterService;
import com.kolyapetrov.telegram_bot.model.service.UserService;
import com.kolyapetrov.telegram_bot.util.MessageUtil;
import com.kolyapetrov.telegram_bot.util.enums.UserState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class EnterCityForFilterAction implements ActionHandler {
    private final UserService userService;
    private final SearchAdsFilterService searchAdsFilterService;

    @Autowired
    public EnterCityForFilterAction(UserService userService, SearchAdsFilterService searchAdsFilterService) {
        this.userService = userService;
        this.searchAdsFilterService = searchAdsFilterService;
    }

    @Override
    public UserState getState() {
        return UserState.ENTER_CITY_FOR_FILTER;
    }

    @Override
    public void handle(Update update, DefaultAbsSender sender) throws TelegramApiException {
        String chatId = update.getMessage().getChatId().toString();
        if (update.getMessage().hasText()) {
            AppUser appUser = userService.getUser(update.getMessage().getFrom());
            String city = update.getMessage().getText();

            // find filter record and setCity for it
            Long recordId = searchAdsFilterService.findRecordByUserId(appUser.getUserId());
            searchAdsFilterService.updateCity(recordId, city);

            appUser.setUserState(UserState.MAIN);
            userService.saveUser(appUser);
            sender.execute(MessageUtil.getMessage(chatId, "Для поиска выбран город: <i>" + city + "</i>", true));

        } else {
            sender.execute(MessageUtil.getMessage(chatId, "Сообщение должно состоять из текста!"));
        }

    }
}
