package com.kolyapetrov.telegram_bot.controller.actions;

import com.kolyapetrov.telegram_bot.controller.ActionHandler;
import com.kolyapetrov.telegram_bot.model.entity.AppUser;
import com.kolyapetrov.telegram_bot.model.entity.Order;
import com.kolyapetrov.telegram_bot.model.entity.UserState;
import com.kolyapetrov.telegram_bot.model.service.UserService;
import com.kolyapetrov.telegram_bot.util.KeyBoardUtil;
import com.kolyapetrov.telegram_bot.util.MessageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Comparator;

@Component
public class EnterDescriptionAction implements ActionHandler {
    private final UserService userService;

    @Autowired
    public EnterDescriptionAction(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void handle(Update update, DefaultAbsSender sender) throws TelegramApiException {
        String chatId = update.getMessage().getChatId().toString();
        AppUser appUser = userService.getUser(update.getMessage().getFrom());

        //TODO: add validation of description for new Ad
        if (update.getMessage().hasText()) {
            String description = update.getMessage().getText();
            var orders = appUser.getOrders()
                    .stream()
                    .sorted(Comparator.comparing(Order::getId))
                    .toList();

            var order = orders.get(orders.size() - 1);
            order.setDescription(description);
            sender.execute(MessageUtil.getMessage(chatId, "Теперь введите город для вашего объявления: "));
            appUser.setUserState(UserState.ENTER_ADDRESS);
            userService.saveUser(appUser);
        } else {
            sender.execute(MessageUtil.getMessage(chatId, "Введите описание для объявления текстом!"));
        }
    }

    @Override
    public UserState getState() {
        return UserState.ENTER_DESCRIPTION_OF_AD;
    }
}
