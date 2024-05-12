package com.kolyapetrov.telegram_bot.util.enums;

import lombok.Getter;

@Getter
public enum BookingStatus {
    NOT_CHECKED("У вас уже есть запрос на бронирование этого периода"),
    ACCEPT("Принято"),
    FORBIDDEN("Арендодатель отклонил ваш запрос на бронирование данного периода");

    private final String name;

    BookingStatus(String name) {
        this.name = name;
    }
}
