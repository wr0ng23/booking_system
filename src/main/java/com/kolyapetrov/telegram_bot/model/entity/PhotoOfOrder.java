package com.kolyapetrov.telegram_bot.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "photos_of_order")
public class PhotoOfOrder {
    @Id
    @Column(name = "id")
    private String id;
}
