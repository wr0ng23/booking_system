package com.kolyapetrov.telegram_bot.util.enums;

import com.kolyapetrov.telegram_bot.util.ConstantMessages;
import lombok.Getter;

@Getter
public enum CallBackName {
    LOCAL_ADS(ConstantMessages.LOCAL_ADS),
    MY_ADS(ConstantMessages.MY_ADS),
    OTHER_ADS(ConstantMessages.OTHER_ADS),
    SELECT_DATE(ConstantMessages.SELECT_DATE),
    ALREADY_SELECTED_DATES(ConstantMessages.ALREADY_SELECTED),
    ALREADY_BOOKED_DATES(ConstantMessages.ALREADY_BOOKED),
    BOOK_BUTTON(ConstantMessages.BOOKING_PRIVATE),
    BACK_FROM_BOOKING(ConstantMessages.BACK_FROM_DATE_CHOOSE),
    ACCEPT_BOOKING(ConstantMessages.ACCEPT_BOOKING_PRIVATE),
    ADMIN_ACTIONS(ConstantMessages.ADMIN_ADS);

    private final String callBackName;

    CallBackName(String callBackName) {
        this.callBackName = callBackName;
    }
}
