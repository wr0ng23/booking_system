package com.kolyapetrov.telegram_bot.controller;

import com.kolyapetrov.telegram_bot.util.Command;

public interface CommandHandler extends Handler {
    Command getCommand();
}
