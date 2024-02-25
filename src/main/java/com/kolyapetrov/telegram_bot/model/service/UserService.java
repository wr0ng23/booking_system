package com.kolyapetrov.telegram_bot.model.service;

import com.kolyapetrov.telegram_bot.model.entity.AppUser;
import com.kolyapetrov.telegram_bot.model.entity.Order;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.List;

public interface UserService {
    void saveUser(AppUser appUser);
    AppUser getUser(User telegramUser);
    List<Order> getOrders(Long id);
    Order getOrderByNumberOfOrder(Long idOfUser, Long numberOfOrder);
}
