package com.kolyapetrov.telegram_bot.model.entity;

import jakarta.persistence.*;
import lombok.*;

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
    @Column(name = "type_of_user")
    private UserType userType;

    @Enumerated(EnumType.STRING)
    @Column(name = "state_of_user")
    private UserState userState;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;
}
