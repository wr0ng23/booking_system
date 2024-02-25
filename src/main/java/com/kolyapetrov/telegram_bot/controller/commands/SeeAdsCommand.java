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
import org.telegram.telegrambots.meta.api.objects.media.InputMedia;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
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

        List<Order> orders = appUser.getOrders();
        for (var order : orders) {
            String description = order.getDescription();
            List<PhotoOfOrder> photosOfOrder = order.getPhotos();
            sender.execute(MessageUtil.getMessage(chatId, "<strong>Это ваше " + order.getNumberOfOrder()
                    + " объявление:</strong>"));

            if (order.getPhotos().size() == 1) {
                String idOfPhoto = order.getPhotos().get(0).getId();
                sender.execute(MessageUtil.getMessage(chatId, description, idOfPhoto));
            } else {
                List<InputMedia> inputMediaPhotos = new ArrayList<>();
                photosOfOrder.forEach(photo -> inputMediaPhotos.add(new InputMediaPhoto(photo.getId())));
                sender.execute(MessageUtil.getMessage(chatId, description, inputMediaPhotos));
            }
        }
        sender.execute(MessageUtil.getMessage(chatId, "<strong>Количество созданных объявлений: "
                        + orders.size() + ".</strong>", KeyBoardUtil.mainKeyBoard()));
    }
}
