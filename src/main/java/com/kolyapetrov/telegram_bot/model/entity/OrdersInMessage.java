package com.kolyapetrov.telegram_bot.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "orders_in_message")
public class OrdersInMessage {
    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "id_of_message")
    private Long messageId;

    @ManyToOne
    @JoinColumn(name = "id_of_order")
    private Order order;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private AppUser user;

    @Column(name = "distance")
    private Double distance;
}
