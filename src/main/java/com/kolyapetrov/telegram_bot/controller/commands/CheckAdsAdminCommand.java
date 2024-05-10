package com.kolyapetrov.telegram_bot.controller.commands;

import com.kolyapetrov.telegram_bot.controller.CommandHandler;
import com.kolyapetrov.telegram_bot.model.service.OrderService;
import com.kolyapetrov.telegram_bot.util.Command;
import com.kolyapetrov.telegram_bot.util.MessageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

@Component
public class CheckAdsAdminCommand implements CommandHandler {
    @Value("${admin.usernames}")
    private List<String> adminUsernames;

    private final OrderService orderService;

    @Autowired
    public CheckAdsAdminCommand(OrderService orderService) {
        this.orderService = orderService;
    }

    @Override
    public Command getCommand() {
        return Command.CHECK_ADS_ADMIN;
    }

    @Override
    public void handle(Update update, DefaultAbsSender sender) throws TelegramApiException {
        String chatId = update.getMessage().getChatId().toString();
        String username = update.getMessage().getFrom().getUserName();

        if (adminUsernames.contains(username)) {
            var orders = orderService.findOrdersByCheckedIsNot();
            if (orders.isEmpty()) {
                sender.execute(MessageUtil.getMessage(chatId, "Пока что нет новых объявлений от пользователей"));

            } else {
                for (var order : orders) {
                    //TODO: create keyboard for administration of orders
                    sender.execute(MessageUtil.getMessage(chatId, order.toString()));
                }
            }
        } else {
            sender.execute(MessageUtil.getMessage(chatId, Command.CHECK_ADS_ADMIN.getCommand()));
        }
    }
}
