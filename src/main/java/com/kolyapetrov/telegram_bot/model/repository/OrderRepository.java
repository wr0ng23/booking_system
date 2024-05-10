package com.kolyapetrov.telegram_bot.model.repository;

import com.kolyapetrov.telegram_bot.model.entity.Order;
import com.kolyapetrov.telegram_bot.util.enums.OrderState;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface OrderRepository extends CrudRepository<Order, Long> {
    @Query("SELECT o FROM Order o WHERE o.city = ?1 AND o.user.userId != ?2 AND o.state = ?3")
    List<Order> findByCityAndUserIdNot(String city, Long userId, OrderState state);

    @Query(value = "select name_of_user " +
            "from users u join orders o on u.id = o.id_of_user " +
            "where o.id = ?1",
            nativeQuery = true)
    String findNameOfUserByOrderId(Long id);

    @Query(value = "select u.id " +
            "from users u join orders o on u.id = o.id_of_user " +
            "where o.id = ?1",
            nativeQuery = true)
    Long findUserIdByOrderId(Long id);

    List<Order> findByState(OrderState state);
}
