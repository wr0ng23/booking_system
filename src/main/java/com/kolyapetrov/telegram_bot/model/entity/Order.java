package com.kolyapetrov.telegram_bot.model.entity;

import com.kolyapetrov.telegram_bot.util.OrderUtil;
import com.kolyapetrov.telegram_bot.util.enums.OrderState;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

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

    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "id_of_order")
    private List<PhotoOfOrder> photos;

    @Column(name = "is_editing")
    private boolean isEditing;

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

    @Enumerated(EnumType.STRING)
    @Column(name = "state")
    private OrderState state;

    @ManyToOne
    @JoinColumn(name = "id_of_user")
    private AppUser user;

    @OneToMany(mappedBy = "order", fetch = FetchType.EAGER)
    List<MetroDistance> metroDistances;

    @Override
    public String toString() {
        String infoAboutMetro = "";
        if (!metroDistances.isEmpty()) {
            infoAboutMetro = getInfoAboutMetro();
        }

        return "<b>Название:</b>\n" + getTitle() + "\n\n" +
                "<b>Описание:</b>\n" + getDescription() + "\n\n" +
                "<b>Город:</b> " + getCity() + "\n" +
                "<b>Адрес:</b> " + getAddress() + "\n\n" +
                "<b>Стоимость проживания:</b> " + getPrice() + " руб./сутки" + "\n\n" +
                infoAboutMetro +
                "<b>Автор объявления:</b> @" + user.getNameOfUser() ;
    }

    private String getInfoAboutMetro() {
        StringBuilder infoAboutmetro = new StringBuilder("<b>Расстояние до ближайших станций метро:</b>\n");
        metroDistances.forEach(metroDistance ->
                infoAboutmetro
                        .append(metroDistance.getMetroInfo().getName())
                        .append(": ")
                        .append(OrderUtil.getDistanceToMetro(metroDistance.getDistance()))
                        .append("\n"));
        return infoAboutmetro.append("\n").toString();
    }

    public String toStringMyAd() {
        return "<b>Статус объявления:</b> " + getState().getName() + "\n\n" +
                "<b>Название:</b>\n" + getTitle() + "\n\n" +
                "<b>Описание:</b>\n" + getDescription() + "\n\n" +
                "<b>Город:</b> " + getCity() + "\n" +
                "<b>Адрес:</b> " + getAddress() + "\n\n" +
                "<b>Стоимость проживания:</b> " + getPrice() + " руб./сутки";
    }
}
