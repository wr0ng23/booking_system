package com.kolyapetrov.telegram_bot.controller.commands;

import com.kolyapetrov.telegram_bot.controller.CommandHandler;
import com.kolyapetrov.telegram_bot.model.entity.AppUser;
import com.kolyapetrov.telegram_bot.model.entity.Order;
import com.kolyapetrov.telegram_bot.model.entity.PhotoOfOrder;
import com.kolyapetrov.telegram_bot.model.service.UserService;
import com.kolyapetrov.telegram_bot.util.Command;
import com.kolyapetrov.telegram_bot.util.KeyBoardUtil;
import com.kolyapetrov.telegram_bot.util.MessageUtil;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Comparator;
import java.util.List;

@Component
public class SeeAdsCommand implements CommandHandler {
    public UserService userService;

    public SeeAdsCommand(UserService userService) {
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
        var firstOrder = orders.get(0);
        String description = firstOrder.getDescription();
        List<PhotoOfOrder> photosOfOrder = firstOrder.getPhotos();
        String mainPhotoId = photosOfOrder.get(0).getId();
        String price = "\n<b>Цена: </b>" + firstOrder.getPrice();
        sender.execute(MessageUtil.getMessage(chatId, description + price, mainPhotoId,
                KeyBoardUtil.seeADsKeyboard(orders.get(orders.size() - 1).getId(), firstOrder.getId(),
                        orders.get(1).getId())));
    }
}
