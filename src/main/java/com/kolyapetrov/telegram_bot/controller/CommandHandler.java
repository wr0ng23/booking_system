package com.kolyapetrov.telegram_bot.controller;

import com.kolyapetrov.telegram_bot.util.Command;

//TODO: maybe make /exit command for clear all data about user
public interface CommandHandler extends Handler {
    Command getCommand();
}
