package com.kolyapetrov.telegram_bot.model.service;

import com.kolyapetrov.telegram_bot.model.entity.Order;

public interface OrderService {
    void saveOrder(Order order);
    Order getOrder(Long id);
}
