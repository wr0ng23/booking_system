package com.kolyapetrov.telegram_bot.controller.actions;

import com.kolyapetrov.telegram_bot.controller.ActionHandler;
import com.kolyapetrov.telegram_bot.model.entity.AppUser;
import com.kolyapetrov.telegram_bot.model.entity.Order;
import com.kolyapetrov.telegram_bot.model.service.UserService;
import com.kolyapetrov.telegram_bot.util.MessageUtil;
import com.kolyapetrov.telegram_bot.util.enums.OrderState;
import com.kolyapetrov.telegram_bot.util.enums.UserState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.LinkedList;

@Component
public class EnterTitleAdAction implements ActionHandler {
    private final UserService userService;

    @Autowired
    public EnterTitleAdAction(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserState getState() {
        return UserState.ENTER_TITLE_OF_AD;
    }

    @Override
    public void handle(Update update, DefaultAbsSender sender) throws TelegramApiException {
        String chatId = update.getMessage().getChatId().toString();
        AppUser appUser = userService.getUser(update.getMessage().getFrom());

        if (update.getMessage().hasText()) {
            String title = update.getMessage().getText();
            Order order = Order.builder()
                    .state(OrderState.NOT_CHECKED)
                    .title(title)
                    .photos(new LinkedList<>())
                    .build();

            appUser.getOrders().add(order);
            appUser.setUserState(UserState.ENTER_DESCRIPTION_OF_AD);
            userService.saveUser(appUser);
            sender.execute(MessageUtil.getMessage(chatId, "Название для объявления установлено, " +
                    "теперь необходимо указать описание для объявления:"));
        } else {
            sender.execute(MessageUtil.getMessage(chatId, "Вам необходимо ввести описание объявления текстом!"));
        }
    }
}
