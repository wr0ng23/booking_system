package com.kolyapetrov.telegram_bot.controller;

import com.kolyapetrov.telegram_bot.controller.messages.ConstantMessages;
import com.kolyapetrov.telegram_bot.model.entity.AppUser;
import com.kolyapetrov.telegram_bot.model.entity.UserState;
import com.kolyapetrov.telegram_bot.model.entity.UserType;
import com.kolyapetrov.telegram_bot.model.service.UserService;
import com.kolyapetrov.telegram_bot.util.KeyBoardUtil;
import com.kolyapetrov.telegram_bot.util.MessageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Location;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class MessageHandler {
    private final UserService userService;

    @Autowired
    public MessageHandler(UserService userService) {
        this.userService = userService;
    }

    public SendMessage handleMessage(Update update) {
        AppUser appUser = userService.getUser(update.getMessage().getFrom());
        UserState userState = appUser.getUserState();

        return switch (userState) {
            case REGISTRATION -> requestTypeOfUser(update, appUser);
            case REQUEST_LOCATION -> infoAboutLocation(update, appUser);
            case MAIN_MENU -> userAccount(update);
        };
    }


    // TODO: make different accounts for landlord and tenant with different features
    private SendMessage userAccount(Update update) {
        String chatId = update.getMessage().getChatId().toString();
        String message = update.getMessage().getText();

        return MessageUtil.getMessage(chatId, message);
    }

    private SendMessage infoAboutLocation(Update update, AppUser appUser) {
        Location location = update.getMessage().getLocation();
        String chatId = update.getMessage().getChatId().toString();

        Double longitude = location.getLongitude();
        Double latitude = location.getLatitude();

        String locationText = "Вы находитесь в месте с кооридинатами: {" + latitude +
                ", " + longitude + "}";

        appUser.setUserState(UserState.MAIN_MENU);
        appUser.setLatitude(latitude);
        appUser.setLongitude(longitude);
        userService.saveUser(appUser);

        return MessageUtil.getMessage(chatId, locationText, KeyBoardUtil.mainKeyBoard());
    }

    private SendMessage requestTypeOfUser(Update update, AppUser appUser) {
        String chatId = update.getMessage().getChatId().toString();
        String message = update.getMessage().getText();

        if (message.equals(ConstantMessages.LANDLORD)) {
            appUser.setUserType(UserType.LANDLORD);
        } else if (message.equals(ConstantMessages.TENANT)) {
            appUser.setUserType(UserType.TENANT);
        }
        appUser.setUserState(UserState.REQUEST_LOCATION);
        userService.saveUser(appUser);

        return MessageUtil.getMessage(chatId, "Пожалуйста, включите геопозицию в настройках " +
                "для корректной обработки вашего местоположения!", KeyBoardUtil.locationKeyBoard());

    }
}
