package com.kolyapetrov.telegram_bot.model.service;

import com.kolyapetrov.telegram_bot.model.entity.Filter;
import com.kolyapetrov.telegram_bot.model.entity.Order;
import com.kolyapetrov.telegram_bot.util.enums.OrderState;

import java.util.List;


public interface OrderService {
    void saveOrder(Order order);
    String findUserNameByOrderId(Long id);
    Order getOrder(Long id);
    void deleteOrder(Order order);
    List<Order> findByCityAndUserIdNot(String city, Long userId);
    Order findOrderById(Long id);
    Long findUserIdByOrderId(Long id);
    List<Order> findByState(OrderState state);
    void deleteOrderByOrderId(Long orderId);
    List<Order> findOrdersByFilter(Filter filter);
}
