package com.kolyapetrov.telegram_bot.controller.actions;

import com.kolyapetrov.telegram_bot.controller.ActionHandler;
import com.kolyapetrov.telegram_bot.model.entity.AppUser;
import com.kolyapetrov.telegram_bot.model.entity.Order;
import com.kolyapetrov.telegram_bot.model.service.MetroDistanceService;
import com.kolyapetrov.telegram_bot.model.service.MetroService;
import com.kolyapetrov.telegram_bot.model.service.UserService;
import com.kolyapetrov.telegram_bot.util.KeyBoardUtil;
import com.kolyapetrov.telegram_bot.util.MessageUtil;
import com.kolyapetrov.telegram_bot.util.enums.UserState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Comparator;

@Component
public class EnterPriceAction implements ActionHandler {
    private final UserService userService;
    private final MetroService metroService;
    private final MetroDistanceService metroDistanceService;

    @Autowired
    public EnterPriceAction(UserService userService, MetroService metroService, MetroDistanceService metroDistanceService) {
        this.userService = userService;
        this.metroService = metroService;
        this.metroDistanceService = metroDistanceService;
    }

    @Override
    public UserState getState() {
        return UserState.ENTER_PRICE;
    }

    @Override
    public void handle(Update update, DefaultAbsSender sender) throws TelegramApiException {
        String chatId = update.getMessage().getChatId().toString();
        AppUser appUser = userService.getUser(update.getMessage().getFrom());

        if (update.getMessage().hasText()) {
            try {
                Long price = Long.valueOf(update.getMessage().getText());
                var orders = appUser.getOrders()
                        .stream()
                        .sorted(Comparator.comparing(Order::getId))
                        .toList();

                var order = orders.get(orders.size() - 1);
                order.setPrice(price);
                sender.execute(MessageUtil.getMessage(chatId, "Объявление успешно создано!",
                        KeyBoardUtil.mainKeyBoard()));
                appUser.setUserState(UserState.MAIN);
                userService.saveUser(appUser);

                var results = metroService.findClosestMetroStationByCords(order.getCity(), order.getLongitude(),
                        order.getLatitude());
                if (results.isEmpty()) return;
                results.forEach(result -> result.setOrder(order));
                metroDistanceService.saveMetroDistances(results);

                results.forEach(result -> System.out.println(result.getMetroInfo().getName() + ": " +
                        result.getDistance() + " м."));

            } catch (NumberFormatException e) {
                sender.execute(MessageUtil.getMessage(chatId, "Введите цену для сдачи вашего жилья. " +
                        "Цена должны быть числом!"));
            }
        } else {
            sender.execute(MessageUtil.getMessage(chatId, "Введите цену для сдачи вашего жилья числом!"));
        }
    }
}
