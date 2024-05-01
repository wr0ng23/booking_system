package com.kolyapetrov.telegram_bot.controller.actions;

import com.kolyapetrov.telegram_bot.model.dto.UserInfo;
import com.kolyapetrov.telegram_bot.model.entity.AppUser;
import com.kolyapetrov.telegram_bot.model.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import static com.kolyapetrov.telegram_bot.util.ConstantMessages.MY_ADS;
import static com.kolyapetrov.telegram_bot.util.ConstantMessages.OTHER_ADS;

@Component
public class CallbackQueriesHandler {
    private final UserService userService;
    private final SeeMyAdsCallbackQuery seeMyAdsCallBackQuery;
    private final SeeOtherAdsCallBackQuery seeOtherAdsCallBackQuery;

    @Autowired
    public CallbackQueriesHandler(UserService userService, SeeMyAdsCallbackQuery seeMyAdsCallBackQuery,
                                  SeeOtherAdsCallBackQuery seeOtherAdsCallBackQuery) {
        this.userService = userService;
        this.seeMyAdsCallBackQuery = seeMyAdsCallBackQuery;
        this.seeOtherAdsCallBackQuery = seeOtherAdsCallBackQuery;
    }

    public void handle(Update update, DefaultAbsSender sender) throws TelegramApiException {
        String dataFromCallBackQuery = update.getCallbackQuery().getData();
        AppUser appUser = userService.getUser(update.getCallbackQuery().getFrom());
        String chatId = update.getCallbackQuery().getFrom().getId().toString();
        Integer messageId = update.getCallbackQuery().getMessage().getMessageId();
        UserInfo userInfo = UserInfo.builder()
                .appUser(appUser)
                .chatId(chatId)
                .messageId(messageId)
                .build();

        if (dataFromCallBackQuery.startsWith(MY_ADS)) {
            seeMyAdsCallBackQuery.handle(userInfo, dataFromCallBackQuery, sender);
        } else if (dataFromCallBackQuery.startsWith(OTHER_ADS)) {
            seeOtherAdsCallBackQuery.handle(userInfo, dataFromCallBackQuery, sender);
        }
    }
}
