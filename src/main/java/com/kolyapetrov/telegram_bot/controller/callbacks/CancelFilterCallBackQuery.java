package com.kolyapetrov.telegram_bot.controller.callbacks;

import com.kolyapetrov.telegram_bot.controller.CallBackHandler;
import com.kolyapetrov.telegram_bot.model.dto.CallBackInfo;
import com.kolyapetrov.telegram_bot.model.dto.UserInfo;
import com.kolyapetrov.telegram_bot.model.entity.AppUser;
import com.kolyapetrov.telegram_bot.model.service.UserService;
import com.kolyapetrov.telegram_bot.util.MessageUtil;
import com.kolyapetrov.telegram_bot.util.enums.CallBackName;
import com.kolyapetrov.telegram_bot.util.enums.UserState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class CancelFilterCallBackQuery implements CallBackHandler {
    private final UserService userService;

    @Autowired
    public CancelFilterCallBackQuery(UserService userService) {
        this.userService = userService;
    }

    @Override
    public CallBackName getCallBack() {
        return CallBackName.CANCEL_FILTER;
    }

    @Override
    public void handle(UserInfo userInfo, CallBackInfo callBackInfo, DefaultAbsSender sender) throws TelegramApiException {
        AppUser user = userInfo.getAppUser();
        user.setUserState(UserState.MAIN);
        userService.saveUser(user);
        DeleteMessage deleteMessage = new DeleteMessage(userInfo.getChatId(), userInfo.getMessageId());
        sender.execute(deleteMessage);
        sender.execute(MessageUtil.getAnswerCallbackQuery(callBackInfo.getId(), "Ввод для фильтра отменен!"));
    }
}
