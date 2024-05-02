package com.kolyapetrov.telegram_bot.controller.commands;

import com.kolyapetrov.telegram_bot.controller.CommandHandler;
import com.kolyapetrov.telegram_bot.util.Command;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

@Component
public class CommandsHandler {
    private final Map<Command, CommandHandler> commandMap;

    public CommandsHandler(List<CommandHandler> commands) {
        this.commandMap = commands.stream().collect(toMap(CommandHandler::getCommand, identity()));
    }

    public CommandHandler retrieveCommand(Command command) {
        return commandMap.getOrDefault(command, null);
    }
}
