package com.kolyapetrov.telegram_bot.controller.actions;

import com.kolyapetrov.telegram_bot.controller.ActionHandler;
import com.kolyapetrov.telegram_bot.model.entity.AppUser;
import com.kolyapetrov.telegram_bot.model.entity.Order;
import com.kolyapetrov.telegram_bot.util.UserState;
import com.kolyapetrov.telegram_bot.model.service.LocationService;
import com.kolyapetrov.telegram_bot.model.service.UserService;
import com.kolyapetrov.telegram_bot.util.MessageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.TimeoutException;

@Component
public class EnterAddressAction implements ActionHandler {
    private final UserService userService;
    private final LocationService locationService;

    @Autowired
    public EnterAddressAction(UserService userService, LocationService locationService) {
        this.userService = userService;
        this.locationService = locationService;
    }

    @Override
    public UserState getState() {
        return UserState.ENTER_ADDRESS;
    }

    @Override
    public void handle(Update update, DefaultAbsSender sender) throws TelegramApiException {
        String chatId = update.getMessage().getChatId().toString();
        AppUser appUser = userService.getUser(update.getMessage().getFrom());

        if (update.getMessage().hasText()) {
            String cityOrAddress = update.getMessage().getText();
            var orders = appUser.getOrders()
                    .stream()
                    .sorted(Comparator.comparing(Order::getId))
                    .toList();

            var order = orders.get(orders.size() - 1);
            if (order.getCity() == null) {
                order.setCity(cityOrAddress);
                sender.execute(MessageUtil.getMessage(chatId, "Введите адрес в формате 'ул. Название, " +
                        "номер дома', например 'ул. Передовиков, 13'"));
                userService.saveUser(appUser);
            } else {
                if (cityOrAddress.matches("^ул\\. [А-Яа-я\\s]+, \\d+$")) {
                    order.setAddress(cityOrAddress);
                    sender.execute(MessageUtil.getMessage(chatId, "Теперь введите цену: "));
                    appUser.setUserState(UserState.ENTER_PRICE);
                    userService.saveUser(appUser);

                    try {
                        Map<String, Double> latAndLot = locationService.getCordsByAddress(order.getCity() +
                                ", " + order.getAddress());
                        order.setLatitude(latAndLot.get("lat"));
                        order.setLongitude(latAndLot.get("lon"));
                        userService.saveUser(appUser);

                    } catch (IOException | InterruptedException | TimeoutException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    sender.execute(MessageUtil.getMessage(chatId, "Введите адрес в формате 'ул. Название, " +
                            "номер дома', например 'ул. Передовиков, 13'"));
                }
            }

        } else {
            sender.execute(MessageUtil.getMessage(chatId, "Введите город и адрес для сдачи вашего жилья текстом!"));
        }
    }
}
