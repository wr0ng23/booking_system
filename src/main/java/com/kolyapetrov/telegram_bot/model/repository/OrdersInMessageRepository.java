package com.kolyapetrov.telegram_bot.model.repository;

import com.kolyapetrov.telegram_bot.model.dto.OrderDistanceDTO;
import com.kolyapetrov.telegram_bot.model.entity.Order;
import com.kolyapetrov.telegram_bot.model.entity.OrdersInMessage;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrdersInMessageRepository extends CrudRepository<OrdersInMessage, Long> {
    @Modifying
    @Transactional
    @Query(value = "INSERT INTO orders_in_message (id_of_message, id_of_order, user_id) VALUES (:messageId, :orderId, " +
            ":userId)", nativeQuery = true)
    void insertOrdersInMessage(@Param("orderId") Long orderId, @Param("messageId") Long messageId,
                               @Param("userId") Long userID);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO orders_in_message (id_of_message, id_of_order, user_id, distance) VALUES (:messageId, :orderId, " +
            ":userId, :distance)", nativeQuery = true)
    void insertOrdersInMessage(@Param("orderId") Long orderId, @Param("messageId") Long messageId,
                               @Param("userId") Long userID, @Param("distance") Double distance);
    @Query(value = "SELECT o from Order o join OrdersInMessage om on o.id = om.order.id WHERE om.messageId = :messageId " +
            "AND om.user.userId = :userId")
    List<Order> findIdsOfOrdersByUserIdAndMessageId(@Param("messageId") Long messageId, @Param("userId") Long userID);

    @Query(value = "SELECT new com.kolyapetrov.telegram_bot.model.dto.OrderDistanceDTO(om.order.id, om.distance) " +
            "FROM OrdersInMessage om WHERE om.messageId = :messageId AND om.user.userId = :userId ORDER BY om.distance")
    List<OrderDistanceDTO> findOrderIdsAndDistancesByUserIdAndMessageId(@Param("messageId") Long messageId,
                                                                        @Param("userId") Long userId);
}
