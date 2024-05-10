package com.kolyapetrov.telegram_bot.util.enums;

import com.kolyapetrov.telegram_bot.util.ConstantMessages;
import lombok.Getter;

@Getter
public enum Command {
    START(ConstantMessages.START),
    CREATE_NEW_ADVERTISEMENT(ConstantMessages.CREATE_NEW_ADVERTISEMENT),
    SEE_MY_ADVERTISEMENTS(ConstantMessages.SEE_MY_ADVERTISEMENTS),
    SEARCH_FOR_ADS(ConstantMessages.SEARCH_BY_ADVERTISEMENTS),
    CHECK_ADS_ADMIN(ConstantMessages.ADMIN_COMMAND);

    private final String command;

    Command(final String command) {
        this.command = command;
    }
}
