package com.kolyapetrov.telegram_bot.controller;

import com.kolyapetrov.telegram_bot.model.entity.UserState;

public interface ActionHandler extends Handler {
    UserState getState();
}
