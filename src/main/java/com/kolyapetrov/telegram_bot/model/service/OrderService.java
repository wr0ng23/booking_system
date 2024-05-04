package com.kolyapetrov.telegram_bot.model.service;

import com.kolyapetrov.telegram_bot.model.entity.Order;

import java.util.List;


public interface OrderService {
    void saveOrder(Order order);
    String findUserNameByOrderId(Long id);
    Order getOrder(Long id);
    void deleteOrder(Order order);
    List<Order> findOrdersByCity(String city);
    Order findOrderById(Long id);
}
