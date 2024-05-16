package com.kolyapetrov.telegram_bot.controller.callbacks;

import com.kolyapetrov.telegram_bot.controller.CallBackHandler;
import com.kolyapetrov.telegram_bot.model.dto.CallBackInfo;
import com.kolyapetrov.telegram_bot.model.dto.UserInfo;
import com.kolyapetrov.telegram_bot.model.entity.AppUser;
import com.kolyapetrov.telegram_bot.model.service.SearchAdsFilterService;
import com.kolyapetrov.telegram_bot.model.service.UserService;
import com.kolyapetrov.telegram_bot.util.KeyBoardUtil;
import com.kolyapetrov.telegram_bot.util.MessageUtil;
import com.kolyapetrov.telegram_bot.util.enums.CallBackName;
import com.kolyapetrov.telegram_bot.util.enums.UserState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import static com.kolyapetrov.telegram_bot.util.ConstantMessages.*;

@Component
public class SearchFilterCallBackQuery implements CallBackHandler {
    private final UserService userService;
    private final SearchAdsFilterService searchAdsFilterService;

    @Autowired
    public SearchFilterCallBackQuery(UserService userService, SearchAdsFilterService searchAdsFilterService) {
        this.userService = userService;
        this.searchAdsFilterService = searchAdsFilterService;
    }

    @Override
    public CallBackName getCallBack() {
        return CallBackName.SEARCH_FILTER;
    }

    @Override
    public void handle(UserInfo userInfo, CallBackInfo callBackInfo, DefaultAbsSender sender) throws TelegramApiException {
        String nameOfButton = callBackInfo.getNameOfButton();

        if (!searchAdsFilterService.isDatabaseCreated()) {
            sender.execute(MessageUtil.getAnswerCallbackQuery(callBackInfo.getId(), "Срок действия фильтра истек"));
            DeleteMessage deleteMessage = new DeleteMessage(userInfo.getChatId(), userInfo.getMessageId());
            sender.execute(deleteMessage);
            return;
        }

        // update 'time of update' filter record
        Long userId = userInfo.getAppUser().getUserId();
        searchAdsFilterService.updateLastUpdateTime(userId);

        switch (nameOfButton) {
            case ENTER_CITY -> enterCity(userInfo, callBackInfo, sender);
            case ENTER_LOWER_PRICE -> enterLowerPrice(userInfo, callBackInfo, sender);
            case ENTER_UPPER_PRICE -> enterUpperPrice(userInfo, callBackInfo, sender);
            case ENTER_LOWER_DATE -> enterLowerDate(userInfo, callBackInfo, sender);
            case ENTER_UPPER_DATE -> enterUpperDate(userInfo, callBackInfo, sender);
            case SEARCH -> search(userInfo, callBackInfo, sender);
        }
    }

    private void enterCity(UserInfo userInfo, CallBackInfo callBackInfo, DefaultAbsSender sender)
            throws TelegramApiException {
        sender.execute(MessageUtil.getMessage(userInfo.getChatId(), "<i>Название города: </i>",
                KeyBoardUtil.cancelFilter()));
        sender.execute(MessageUtil.getAnswerCallbackQuery(callBackInfo.getId(), "Введите название города!"));

        AppUser user = userInfo.getAppUser();
        user.setUserState(UserState.ENTER_CITY_FOR_FILTER);
        userService.saveUser(user);
    }

    private void enterLowerPrice(UserInfo userInfo, CallBackInfo callBackInfo, DefaultAbsSender sender)
            throws TelegramApiException {

    }

    private void enterUpperPrice(UserInfo userInfo, CallBackInfo callBackInfo, DefaultAbsSender sender)
            throws TelegramApiException {

    }

    private void enterLowerDate(UserInfo userInfo, CallBackInfo callBackInfo, DefaultAbsSender sender)
            throws TelegramApiException {

    }

    private void enterUpperDate(UserInfo userInfo, CallBackInfo callBackInfo, DefaultAbsSender sender)
            throws TelegramApiException {

    }

    private void search(UserInfo userInfo, CallBackInfo callBackInfo, DefaultAbsSender sender)
            throws TelegramApiException {

    }
}
