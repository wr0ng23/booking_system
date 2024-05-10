package com.kolyapetrov.telegram_bot.model.repository;

import com.kolyapetrov.telegram_bot.model.entity.Order;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface OrderRepository extends CrudRepository<Order, Long> {
    List<Order> findByCity(String city);

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
}
