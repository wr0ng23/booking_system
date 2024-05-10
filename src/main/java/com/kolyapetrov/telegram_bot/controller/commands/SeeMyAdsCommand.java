package com.kolyapetrov.telegram_bot.controller.commands;

import com.kolyapetrov.telegram_bot.controller.CommandHandler;
import com.kolyapetrov.telegram_bot.model.entity.AppUser;
import com.kolyapetrov.telegram_bot.model.entity.Order;
import com.kolyapetrov.telegram_bot.model.entity.PhotoOfOrder;
import com.kolyapetrov.telegram_bot.model.service.UserService;
import com.kolyapetrov.telegram_bot.util.*;
import com.kolyapetrov.telegram_bot.util.enums.Command;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Comparator;
import java.util.List;

@Component
public class SeeMyAdsCommand implements CommandHandler {
    public UserService userService;

    public SeeMyAdsCommand(UserService userService) {
        this.userService = userService;
    }

    @Override
    public Command getCommand() {
        return Command.SEE_MY_ADVERTISEMENTS;
    }

    @Override
    public void handle(Update update, DefaultAbsSender sender) throws TelegramApiException {
        AppUser appUser = userService.getUser(update.getMessage().getFrom());
        String chatId = update.getMessage().getChatId().toString();

        List<Order> orders = appUser.getOrders().stream().sorted(Comparator.comparing(Order::getId)).toList();
        if (orders.isEmpty()) {
            sender.execute(MessageUtil.getMessage(chatId, "Пока что у вас нет созданных объявлений!"));

        } else {
            var firstOrder = orders.get(0);
            List<PhotoOfOrder> photosOfOrder = firstOrder.getPhotos();
            String mainPhotoId = photosOfOrder.get(0).getId();

            if (orders.size() > 1) {
                Long leftOrderId = orders.get(orders.size() - 1).getId();
                Long currentOrderId = orders.get(0).getId();
                Long rightOrderId = orders.get(1).getId();

                sender.execute(MessageUtil.getMessage(chatId, String.valueOf(firstOrder), mainPhotoId,
                        KeyBoardUtil.seeMyADsKeyboard(leftOrderId, currentOrderId, rightOrderId)));
            } else {
                sender.execute(MessageUtil.getMessage(chatId, String.valueOf(firstOrder), mainPhotoId,
                        KeyBoardUtil.seeMyADsKeyboard(orders.get(0).getId())));
            }
        }
    }
}
