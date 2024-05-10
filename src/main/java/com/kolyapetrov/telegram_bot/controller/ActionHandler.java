package com.kolyapetrov.telegram_bot.controller;

import com.kolyapetrov.telegram_bot.util.enums.UserState;

public interface ActionHandler extends Handler {
    UserState getState();
}
