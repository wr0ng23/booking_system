package com.kolyapetrov.telegram_bot.util;

import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.media.InputMedia;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

import java.io.File;
import java.util.List;

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

    public static SendPhoto getMessage(String chatId, File photo) {
        return new SendPhoto(chatId, new InputFile(photo));
    }

    public static SendPhoto getMessage(String chatId, String description, String photoId) {
        SendPhoto newSendPhoto = new SendPhoto();
        newSendPhoto.setChatId(chatId);
        newSendPhoto.setCaption(description);
        InputFile inputFile = new InputFile();
        inputFile.setMedia(photoId);
        newSendPhoto.setPhoto(inputFile);
        return newSendPhoto;
    }

    public static SendPhoto getMessage(String chatId, String message, File photo) {
        SendPhoto sendPhoto = new SendPhoto(chatId, new InputFile(photo));
        sendPhoto.setCaption(message);
        return sendPhoto;
    }

    public static SendPhoto getMessage(String chatId, String message, File photo,
                                       ReplyKeyboardMarkup replyKeyboardMarkup) {
        var replyMessage = getMessage(chatId, message, photo);
        replyMessage.setReplyMarkup(replyKeyboardMarkup);
        return replyMessage;
    }

    private static SendMessage getSomeMessage(String chatId, String message) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(message);
        sendMessage.enableHtml(true);
        return sendMessage;
    }

    public static SendMediaGroup getMessage(String chatId, String description, List<InputMedia> inputMediaPhotos) {
        SendMediaGroup sendMediaGroup = new SendMediaGroup();
        inputMediaPhotos.get(0).setCaption(description);
        sendMediaGroup.setMedias(inputMediaPhotos);
        sendMediaGroup.setChatId(chatId);
        return sendMediaGroup;
    }
}
