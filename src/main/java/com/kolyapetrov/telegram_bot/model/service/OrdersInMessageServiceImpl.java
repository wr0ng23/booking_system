package com.kolyapetrov.telegram_bot.model.service;


import com.kolyapetrov.telegram_bot.model.dto.OrderDistanceDTO;
import com.kolyapetrov.telegram_bot.model.entity.Order;
import com.kolyapetrov.telegram_bot.model.repository.OrdersInMessageRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrdersInMessageServiceImpl implements OrdersInMessageService {
    private final OrdersInMessageRepository ordersInMessageRepository;

    public OrdersInMessageServiceImpl(OrdersInMessageRepository ordersInMessageRepository) {
        this.ordersInMessageRepository = ordersInMessageRepository;
    }

    @Override
    public void saveAll(List<Long> orderIdList, Long messageId, Long userId) {
        orderIdList.forEach(id -> ordersInMessageRepository.insertOrdersInMessage(id, messageId, userId));
    }

    @Override
    public void saveAll(List<Long> orderIdList, Long messageId, Long userId, List<Double> distances) {
        for (int i = 0; i < orderIdList.size(); ++i) {
            ordersInMessageRepository.insertOrdersInMessage(orderIdList.get(i), messageId, userId, distances.get(i));
        }
    }

    @Override
    public List<Order> findOrdersByMessageIdAndUserId(Long messageId, Long userId) {
        return ordersInMessageRepository.findIdsOfOrdersByUserIdAndMessageId(messageId, userId);
    }

    @Override
    public List<OrderDistanceDTO> findOrderIdsAndDistancesByUserIdAndMessageId(Long messageId, Long userId) {
        return ordersInMessageRepository.findOrderIdsAndDistancesByUserIdAndMessageId(messageId, userId);
    }
}
