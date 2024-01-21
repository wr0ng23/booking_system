package com.kolyapetrov.telegram_bot;

import com.kolyapetrov.telegram_bot.config.BotConfig;
import com.kolyapetrov.telegram_bot.controller.CommandHandler;
import com.kolyapetrov.telegram_bot.controller.MessageHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class BookingBot extends TelegramLongPollingBot {
    public static String COMMAND_PREFIX = "/";
    private final BotConfig botConfig;
    private final MessageHandler messageHandler;
    private final CommandHandler commandHandler;

    @Autowired
    public BookingBot(BotConfig botConfig, MessageHandler messageHandler,
                      CommandHandler commandHandler) {
        super(botConfig.getToken());
        this.botConfig = botConfig;
        this.messageHandler = messageHandler;
        this.commandHandler = commandHandler;
    }

    @Override
    public String getBotUsername() {
        return botConfig.getName();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String message = update.getMessage().getText();

            if (message.startsWith(COMMAND_PREFIX)) {
                String commandIdentifier = message.split(" ")[0].toLowerCase();
                SendMessage commandMessage = commandHandler.retrieveCommand(commandIdentifier)
                        .retrieveMessage(update);
                sendMessage(commandMessage);
            } else {
                SendMessage replyMessage = messageHandler.handleMessage(update);
                sendMessage(replyMessage);
            }

        } else if (update.getMessage().hasLocation()) {
            SendMessage replyMessage = messageHandler.handleMessage(update);
            sendMessage(replyMessage);
        }
    }

    public void sendMessage(SendMessage sendMessage) {
        try {
            this.execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
}
