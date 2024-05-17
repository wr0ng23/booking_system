package com.kolyapetrov.telegram_bot.model.service;

import com.kolyapetrov.telegram_bot.model.dto.OrderDistanceDTO;
import com.kolyapetrov.telegram_bot.model.entity.Order;

import java.util.List;

public interface OrdersInMessageService {
    void saveAll(List<Long> orderIdList, Long messageId, Long userId);
    void saveAll(List<Long> orderIdList, Long messageId, Long userId, List<Double> distances);
    List<Order> findOrdersByMessageIdAndUserId(Long messageId, Long userId);

    List<OrderDistanceDTO> findOrderIdsAndDistancesByUserIdAndMessageId(Long messageId, Long userId);
}
