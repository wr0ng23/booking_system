package com.kolyapetrov.telegram_bot.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "orders")
public class
Order {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "description")
    private String description;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "id_of_order")
    private List<PhotoOfOrder> photos;

    @Column(name = "is_editing")
    private Boolean isEditing;

    @Column(name = "price")
    private Long price;

    @Column(name = "city")
    private String city;

    @Column(name = "address")
    private String address;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "latitude")
    private Double latitude;

    @OneToMany(mappedBy = "order")
    Set<Booking> bookings;

    @Override
    public String toString() {
        return "<b>Описание:</b>\n" + getDescription() + "\n\n" +
                "<b>Цена:</b> " + getPrice() + " руб." + "\n" +
                "<b>Город:</b> " + getCity() + "\n" +
                "<b>Адрес:</b> " + getAddress();
    }
}
