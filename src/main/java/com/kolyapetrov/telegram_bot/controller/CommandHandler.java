package com.kolyapetrov.telegram_bot.controller;

import com.kolyapetrov.telegram_bot.controller.commands.CommandsNames;
import com.kolyapetrov.telegram_bot.controller.commands.Executable;
import com.kolyapetrov.telegram_bot.controller.commands.StartCommand;
import com.kolyapetrov.telegram_bot.controller.commands.UnknownCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class CommandHandler {
    private final Map<String, Executable> commandMap;
    private final UnknownCommand unknownCommand;

    @Autowired
    public CommandHandler(StartCommand startCommand, UnknownCommand unknownCommand) {
        commandMap = Map.ofEntries(
                Map.entry(CommandsNames.START.getCommand(), startCommand)
        );

        this.unknownCommand = unknownCommand;
    }

    public Executable retrieveCommand(String commandName) {
        return commandMap.getOrDefault(commandName, unknownCommand);
    }
}
