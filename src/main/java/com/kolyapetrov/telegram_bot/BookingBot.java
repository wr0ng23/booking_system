package com.kolyapetrov.telegram_bot;

import com.kolyapetrov.telegram_bot.config.BotConfig;
import com.kolyapetrov.telegram_bot.controller.CommandHandler;
import com.kolyapetrov.telegram_bot.controller.ActionHandler;
import com.kolyapetrov.telegram_bot.controller.actions.ActionsHandlerContainer;
import com.kolyapetrov.telegram_bot.controller.actions.CallbackQueriesHandler;
import com.kolyapetrov.telegram_bot.controller.commands.*;
import com.kolyapetrov.telegram_bot.model.entity.AppUser;
import com.kolyapetrov.telegram_bot.util.UserState;
import com.kolyapetrov.telegram_bot.model.service.UserService;
import com.kolyapetrov.telegram_bot.util.Command;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Arrays;
import java.util.Optional;

@Component
public class BookingBot extends TelegramLongPollingBot {
    private final BotConfig botConfig;
    private final CommandsHandlerContainer commandsHandlerContainer;
    private final ActionsHandlerContainer actionsHandlerContainer;
    private final UserService userService;
    private final CallbackQueriesHandler callbackQueriesHandler;

    @Autowired
    public BookingBot(BotConfig botConfig, CommandsHandlerContainer CommandsHandlerContainer,
                      ActionsHandlerContainer actionsHandlerContainer, UserService userService,
                      CallbackQueriesHandler callbackQueriesHandler) {
        super(botConfig.getToken());
        this.botConfig = botConfig;
        this.commandsHandlerContainer = CommandsHandlerContainer;
        this.actionsHandlerContainer = actionsHandlerContainer;
        this.userService = userService;
        this.callbackQueriesHandler = callbackQueriesHandler;
    }

    @Override
    public String getBotUsername() {
        return botConfig.getName();
    }

    @Override
    public void onUpdateReceived(Update update) {
        try {
            handle(update);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private void handle(Update update) throws TelegramApiException {
        if (update.hasCallbackQuery()) {
            handleCallBackQuery(update);
            return;
        }
        if (handleCommand(update)) return;
        handleAction(update);
    }

    private boolean handleCommand(Update update) throws TelegramApiException {
        if (!(update.hasMessage() && update.getMessage().hasText())) return false;

        String commandAlias = update.getMessage().getText();
        Optional<Command> commandOptional = Arrays.stream(Command.values())
                .filter(c -> c.getCommand().equals(commandAlias))
                .findFirst();

        Command command;
        if (commandOptional.isEmpty()) {
            return false;
        } else {
            command = commandOptional.get();
        }

        CommandHandler commandHandler = commandsHandlerContainer.retrieveCommand(command);
        if (commandHandler != null) {
            commandHandler.handle(update, this);
            return true;

        } else return false;
    }

    private void handleAction(Update update) throws TelegramApiException {
        AppUser appUser = userService.getUser(update.getMessage().getFrom());
        UserState userState = appUser.getUserState();
        ActionHandler actionHandler = actionsHandlerContainer.retrieveAction(userState);
        if (actionHandler != null) {
            actionHandler.handle(update, this);
        }
    }

    private void handleCallBackQuery(Update update) throws TelegramApiException {
        callbackQueriesHandler.handle(update, this);
    }
}
