package com.kolyapetrov.telegram_bot.util;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class BookingTemp {
    private Long id;
    private Long userId;
    private Long orderId;
    private LocalDate startDate;
    private LocalDate endDate;
}
