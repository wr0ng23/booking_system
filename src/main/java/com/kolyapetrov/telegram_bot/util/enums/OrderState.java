package com.kolyapetrov.telegram_bot.util.enums;

import lombok.Getter;

@Getter
public enum OrderState {
    NOT_CHECKED("На проверке"),
    CHECKED("Проверено администратором"),
    FORBIDDEN("Отклонено администратором");

    private final String name;

    OrderState(String name) {
        this.name = name;
    }
}
