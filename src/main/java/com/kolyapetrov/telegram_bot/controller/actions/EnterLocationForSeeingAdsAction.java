package com.kolyapetrov.telegram_bot.controller.actions;

import com.kolyapetrov.telegram_bot.controller.ActionHandler;
import com.kolyapetrov.telegram_bot.model.entity.AppUser;
import com.kolyapetrov.telegram_bot.model.entity.Order;
import com.kolyapetrov.telegram_bot.model.entity.PhotoOfOrder;
import com.kolyapetrov.telegram_bot.model.service.OrderService;
import com.kolyapetrov.telegram_bot.model.service.UserService;
import com.kolyapetrov.telegram_bot.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.meta.api.objects.Location;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

import static com.kolyapetrov.telegram_bot.util.ConstantMessages.GO_BACK;
import static com.kolyapetrov.telegram_bot.util.UserState.MAIN;
import static com.kolyapetrov.telegram_bot.util.UserState.SEARCH_FOR_ADS;

@Component
public class EnterLocationForSeeingAdsAction implements ActionHandler {
    private final UserService userService;
    private final OrderService orderService;

    @Autowired
    public EnterLocationForSeeingAdsAction(UserService userService, OrderService orderService) {
        this.userService = userService;
        this.orderService = orderService;
    }

    @Override
    public UserState getState() {
        return SEARCH_FOR_ADS;
    }

    @Override
    public void handle(Update update, DefaultAbsSender sender) throws TelegramApiException {
        String chatId = update.getMessage().getChatId().toString();
        AppUser appUser = userService.getUser(update.getMessage().getFrom());

        if (update.getMessage().hasText() && update.getMessage().getText().startsWith(GO_BACK)) {
            appUser.setUserState(MAIN);
            userService.saveUser(appUser);
            sender.execute(MessageUtil.getMessage(chatId, GO_BACK, KeyBoardUtil.mainKeyBoard()));

        } else if (update.getMessage().hasLocation()) {
            Location location = update.getMessage().getLocation();
            System.out.println("lat: " + location.getLatitude());
            System.out.println("lon: " + location.getLongitude());

            appUser.setUserState(MAIN);
            userService.saveUser(appUser);

        } else if (update.getMessage().hasText()) {
            String city = update.getMessage().getText();

            List<Order> orders = orderService.findOrdersByCity(city);
            if (orders.isEmpty()) {
                sender.execute(MessageUtil.getMessage(chatId, "В выбранном городе объявлений не найдено!"));
            } else {
                var firstOrder = orders.get(0);
                String description = firstOrder.getDescription();
                List<PhotoOfOrder> photosOfOrder = firstOrder.getPhotos();
                String mainPhotoId = photosOfOrder.get(0).getId();
                String price = "\n<b>Цена: </b>" + firstOrder.getPrice();

                if (orders.size() > 1) {
                    Long leftOrderId = orders.get(orders.size() - 1).getId();
                    Long currentOrderId = orders.get(0).getId();
                    Long rightOrderId = orders.get(1).getId();
                    sender.execute(MessageUtil.getMessage(chatId, description + price, mainPhotoId,
                            KeyBoardUtil.seeOtherADsKeyboard(leftOrderId, currentOrderId, rightOrderId, city)));
                } else {
                    sender.execute(MessageUtil.getMessage(chatId, description + price, mainPhotoId,
                            KeyBoardUtil.seeOtherADsKeyboard(orders.get(0).getId(), city)));
                }
            }
        } else {
            sender.execute(MessageUtil.getMessage(chatId, "Введите название города текстом!"));
        }
    }
}
