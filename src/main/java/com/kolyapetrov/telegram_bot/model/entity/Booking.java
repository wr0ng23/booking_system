package com.kolyapetrov.telegram_bot.model.entity;

import com.kolyapetrov.telegram_bot.util.enums.BookingStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "booking")
public class Booking {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_of_order")
    private Order order;

    @ManyToOne
    @JoinColumn(name = "id_of_user")
    private AppUser user;

    @Column(name = "date_start")
    private LocalDate dateStart;

    @Column(name = "date_end")
    private LocalDate dateEnd;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private BookingStatus status;
}
