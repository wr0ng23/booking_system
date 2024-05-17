package com.kolyapetrov.telegram_bot.model.repository;

import com.kolyapetrov.telegram_bot.model.entity.Order;
import com.kolyapetrov.telegram_bot.util.enums.OrderState;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;
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

    @Query(value = "select * from orders o join booking b on o.id = b.id_of_order " +
            "where o.price > ?1 and o.price < ?2", nativeQuery = true)
    List<Order> findOrdersByFilter();

    @Query("SELECT o FROM Order o WHERE " +
            "(:city IS NULL OR o.city = :city) " +
            "AND (o.user.userId != :userId)" +
            "AND (o.state = :state)" +
            "AND (:lowerPrice IS NULL OR o.price >= :lowerPrice) " +
            "AND (:upperPrice IS NULL OR o.price <= :upperPrice) " +
            "AND NOT EXISTS (SELECT b FROM Booking b WHERE b.order = o " +
            "AND :upperDate >= b.dateStart AND :lowerDate <= b.dateEnd)")
    List<Order> findOrdersByFilter(String city, Long lowerPrice, Long upperPrice, LocalDate lowerDate, LocalDate upperDate,
                                   Long userId, OrderState state);
}
