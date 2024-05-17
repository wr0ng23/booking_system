package com.kolyapetrov.telegram_bot.model.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class Filter {
    private Long id;
    private Long userId;
    private String city;
    private Long lowerPrice;
    private Long upperPrice;
    private LocalDate lowerDate;
    private LocalDate upperDate;
    private Double longitude;
    private Double latitude;
    private Double distance;
}
