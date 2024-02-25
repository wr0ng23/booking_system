package com.kolyapetrov.telegram_bot.util;

import com.kolyapetrov.telegram_bot.util.ConstantMessages;
import lombok.Getter;

@Getter
public enum Command {
    START(ConstantMessages.START),
    CREATE_NEW_ADVERTISEMENT(ConstantMessages.CREATE_NEW_ADVERTISEMENT),
    SEE_MY_ADVERTISEMENTS(ConstantMessages.SEE_MY_ADVERTISEMENTS);

    private final String command;

    Command(final String command) {
        this.command = command;
    }
}
