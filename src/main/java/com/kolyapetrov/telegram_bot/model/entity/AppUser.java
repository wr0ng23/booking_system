package com.kolyapetrov.telegram_bot.model.entity;

import com.kolyapetrov.telegram_bot.util.UserState;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
public class AppUser {
    @Id
    @Column(name = "id")
    private Long userId;

    @Column(name = "name_of_user")
    private String nameOfUser;

    @Enumerated(EnumType.STRING)
    @Column(name = "state_of_user")
    private UserState userState;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "id_of_user")
    private List<Order> orders;

    @OneToMany(mappedBy = "user")
    Set<Booking> bookings;
}
