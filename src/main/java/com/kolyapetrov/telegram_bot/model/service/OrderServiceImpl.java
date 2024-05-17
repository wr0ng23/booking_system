package com.kolyapetrov.telegram_bot.model.service;

import com.kolyapetrov.telegram_bot.model.entity.Filter;
import com.kolyapetrov.telegram_bot.model.entity.Order;
import com.kolyapetrov.telegram_bot.model.repository.OrderRepository;
import com.kolyapetrov.telegram_bot.util.enums.OrderState;
import com.kolyapetrov.telegram_bot.util.enums.UserState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public String findUserNameByOrderId(Long id) {
        return orderRepository.findNameOfUserByOrderId(id);
    }

    @Override
    public List<Order> findByCityAndUserIdNot(String city, Long userId) {
        return orderRepository.findByCityAndUserIdNot(city, userId, OrderState.CHECKED);
    }

    @Override
    public void saveOrder(Order order) {
        orderRepository.save(order);
    }

    @Override
    public Order getOrder(Long id) {
        return orderRepository.findById(id).orElse(null);
    }

    @Override
    public void deleteOrder(Order order) {
        orderRepository.delete(order);
    }
    @Override
    public void deleteOrderByOrderId(Long orderId) {
        Order order = orderRepository.findById(orderId).get();
        orderRepository.delete(order);
    }

    @Override
    public Long findUserIdByOrderId(Long id) {
        return orderRepository.findUserIdByOrderId(id);
    }

    @Override
    public Order findOrderById(Long id) {
        return orderRepository.findById(id).orElse(null);
    }

    @Override
    public List<Order> findByState(OrderState state) {
        return orderRepository.findByState(state);
    }

    @Override
    public List<Order> findOrdersByFilter(Filter filter) {
        String city = filter.getCity();
        LocalDate lowerDate = filter.getLowerDate();
        LocalDate upperDate = filter.getUpperDate();
        Long lowerPrice = filter.getLowerPrice();
        Long upperPrice = filter.getUpperPrice();
        Long userId = filter.getUserId();

        return orderRepository.findOrdersByFilter(city, lowerPrice, upperPrice, lowerDate, upperDate, userId,
                OrderState.CHECKED);
    }
}
