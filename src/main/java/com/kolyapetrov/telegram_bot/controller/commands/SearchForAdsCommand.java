package com.kolyapetrov.telegram_bot.controller.commands;

import com.kolyapetrov.telegram_bot.controller.CommandHandler;
import com.kolyapetrov.telegram_bot.model.entity.AppUser;
import com.kolyapetrov.telegram_bot.model.service.SearchAdsFilterService;
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
    private final SearchAdsFilterService searchAdsFilterService;

    @Autowired
    public SearchForAdsCommand(UserService userService, SearchAdsFilterService searchAdsFilterService) {
        this.userService = userService;
        this.searchAdsFilterService = searchAdsFilterService;
    }

    @Override
    public Command getCommand() {
        return Command.SEARCH_FOR_ADS;
    }

    @Override
    public void handle(Update update, DefaultAbsSender sender) throws TelegramApiException {
        AppUser appUser = userService.getUser(update.getMessage().getFrom());
        String chatId = update.getMessage().getChatId().toString();

        searchAdsFilterService.createTempTable();
        Long recordId = searchAdsFilterService.insertNewRecord(appUser.getUserId());

        sender.execute(MessageUtil.getMessage(chatId, "Вы можете воспользоваться приведенными ниже фильтрами " +
                        "для поиск объявлений", KeyBoardUtil.filterForAds(recordId)));
        userService.saveUser(appUser);
    }
}
