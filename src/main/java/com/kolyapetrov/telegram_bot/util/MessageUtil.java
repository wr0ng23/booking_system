package com.kolyapetrov.telegram_bot.util;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;

public class MessageUtil {
    private MessageUtil() {
    }

    public static SendMessage getMessage(String chatId, String message,
                                         ReplyKeyboard replyKeyboardMarkup) {
        var replyMessage = getSomeMessage(chatId, message);
        replyMessage.setReplyMarkup(replyKeyboardMarkup);
        return replyMessage;
    }

    public static SendMessage getMessage(String chatId, String message, Integer messageId,
                                         ReplyKeyboard replyKeyboardMarkup) {
        var replyMessage = getSomeMessage(chatId, message);
        replyMessage.setReplyMarkup(replyKeyboardMarkup);
        replyMessage.setReplyToMessageId(messageId);
        return replyMessage;
    }

    public static SendMessage getMessage(String chatId, String message) {
        return getSomeMessage(chatId, message);
    }

    private static SendMessage getSomeMessage(String chatId, String message) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(message);
        sendMessage.enableHtml(true);
        return sendMessage;
    }
}
