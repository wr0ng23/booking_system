package com.kolyapetrov.telegram_bot.controller.commands;

import lombok.Getter;

@Getter
public enum CommandsNames {
    START("/start");

    private final String command;

    CommandsNames(final String command) {
        this.command = command;
    }
}
