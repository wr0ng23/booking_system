package com.kolyapetrov.telegram_bot;

import com.kolyapetrov.telegram_bot.config.BotConfig;
import com.kolyapetrov.telegram_bot.controller.ActionHandler;
import com.kolyapetrov.telegram_bot.controller.CallBackHandler;
import com.kolyapetrov.telegram_bot.controller.CommandHandler;
import com.kolyapetrov.telegram_bot.controller.actions.ActionsHandlerContainer;
import com.kolyapetrov.telegram_bot.controller.CallBackHandlers.CallbackQueriesHandler;
import com.kolyapetrov.telegram_bot.controller.commands.CommandsHandlerContainer;
import com.kolyapetrov.telegram_bot.model.dto.CallBackInfo;
import com.kolyapetrov.telegram_bot.model.dto.UserInfo;
import com.kolyapetrov.telegram_bot.model.entity.AppUser;
import com.kolyapetrov.telegram_bot.model.service.UserService;
import com.kolyapetrov.telegram_bot.util.CallBackName;
import com.kolyapetrov.telegram_bot.util.Command;
import com.kolyapetrov.telegram_bot.util.ConstantMessages;
import com.kolyapetrov.telegram_bot.util.UserState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
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
        if (handleCallBackQuery(update)) return;
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

    private boolean handleCallBackQuery(Update update) throws TelegramApiException {
        if (!update.hasCallbackQuery()) return false;

        String dataFromCallBack = update.getCallbackQuery().getData();
        String typeOfCallBackQuery = dataFromCallBack.split(" ")[0];
        Optional<CallBackName> callBackNameOptional = Arrays.stream(CallBackName.values())
                .filter(c -> c.getCallBackName().equals(typeOfCallBackQuery))
                .findFirst();

        CallBackName callBackName;
        if (callBackNameOptional.isEmpty()) {
            return false;
        } else {
            callBackName = callBackNameOptional.get();
        }

        CallBackHandler callBackHandler = callbackQueriesHandler.retrieveCallBack(callBackName);
        if (callBackHandler != null) {

            callBackHandler.handle(getUserInfo(update), getCallBackInfo(dataFromCallBack), this);
            return true;
        } else return false;
    }

    private UserInfo getUserInfo(Update update) {
        var callBack = update.getCallbackQuery();
        AppUser appUser = userService.getUser(callBack.getFrom());
        String chatId = callBack.getFrom().getId().toString();
        Integer messageId = callBack.getMessage().getMessageId();

        return UserInfo.builder()
                .appUser(appUser)
                .chatId(chatId)
                .messageId(messageId)
                .build();
    }

    private CallBackInfo getCallBackInfo(String callBack) {
        String[] callBackParts = callBack.split(" ");
        CallBackInfo callBackInfo =  CallBackInfo.builder()
                .nameOfButton(callBackParts[1])
                .numberOfOrder(Long.parseLong(callBackParts[2]))
                .build();

        if (callBackParts[0].equals(ConstantMessages.OTHER_ADS)) {
            callBackInfo.setCity(callBackParts[3]);
        } else if (callBackParts[0].equals(ConstantMessages.LOCAL_ADS)) {
            callBackInfo.setLatitude(Double.parseDouble(callBackParts[3]));
            callBackInfo.setLongitude(Double.parseDouble(callBackParts[4]));
        }
        return callBackInfo;
    }
}
