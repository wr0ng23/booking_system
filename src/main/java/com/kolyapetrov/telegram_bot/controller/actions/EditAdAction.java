package com.kolyapetrov.telegram_bot.controller.actions;

import com.kolyapetrov.telegram_bot.controller.ActionHandler;
import com.kolyapetrov.telegram_bot.model.entity.AppUser;
import com.kolyapetrov.telegram_bot.model.entity.Order;
import com.kolyapetrov.telegram_bot.util.enums.UserState;
import com.kolyapetrov.telegram_bot.model.service.UserService;
import com.kolyapetrov.telegram_bot.util.KeyBoardUtil;
import com.kolyapetrov.telegram_bot.util.MessageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class EditAdAction implements ActionHandler {
    private final UserService userService;

    @Autowired
    public EditAdAction(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserState getState() {
        return UserState.EDIT_AD;
    }

    @Override
    public void handle(Update update, DefaultAbsSender sender) throws TelegramApiException {
        String chatId = update.getMessage().getChatId().toString();
        AppUser appUser = userService.getUser(update.getMessage().getFrom());

        if (update.getMessage().hasText()) {
            String description = update.getMessage().getText();
            var orderForEdit = appUser.getOrders().stream().filter(Order::isEditing).findFirst().get();
            orderForEdit.setDescription(description);
            orderForEdit.setEditing(false);
            sender.execute(MessageUtil.getMessage(chatId, "Описание успешно обновлено!", KeyBoardUtil.mainKeyBoard()));
            appUser.setUserState(UserState.MAIN);
            userService.saveUser(appUser);
        } else {
            sender.execute(MessageUtil.getMessage(chatId, "Введите новое описание для объявления текстом!"));
        }
    }
}
