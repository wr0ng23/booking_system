package com.kolyapetrov.telegram_bot.util;

import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageCaption;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageMedia;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.media.InputMedia;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
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
        replyMessage.enableHtml(true);
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

    public static SendMessage getMessage(String chatId, String message, boolean isHtml) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(message);
        sendMessage.enableHtml(isHtml);
        return sendMessage;
    }

    public static AnswerCallbackQuery getAnswerCallbackQuery(String id, String text) {
        return AnswerCallbackQuery.builder()
                .text(text)
                .showAlert(true)
                .callbackQueryId(id)
                .build();
    }

    public static SendPhoto getMessage(String chatId, File photo) {
        return new SendPhoto(chatId, new InputFile(photo));
    }

    public static SendPhoto getMessage(String chatId, String description, String photoId, ReplyKeyboard replyKeyboardMarkup) {
        SendPhoto newSendPhoto = new SendPhoto();
        newSendPhoto.setChatId(chatId);
        newSendPhoto.setCaption(description);
        newSendPhoto.setParseMode("html");
        InputFile inputFile = new InputFile();
        inputFile.setMedia(photoId);
        newSendPhoto.setPhoto(inputFile);
        newSendPhoto.setReplyMarkup(replyKeyboardMarkup);
        return newSendPhoto;
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
//        sendMessage.enableHtml(true);
        return sendMessage;
    }

    public static EditMessageCaption getEditCaptionMessage(String chatId, Integer messageId, String text,
                                                           InlineKeyboardMarkup keyboardMarkup) {
        EditMessageCaption sendMessage = new EditMessageCaption();
        sendMessage.setChatId(chatId);
        sendMessage.setMessageId(messageId);
        sendMessage.setCaption(text);
        sendMessage.setReplyMarkup(keyboardMarkup);
        return sendMessage;
    }

    public static EditMessageMedia getEditMessageForSeeAds(String chatId, Integer messageId, String photoId,
                                                           String description, InlineKeyboardMarkup keyboard) {
        EditMessageMedia sendMessage = new EditMessageMedia();
        sendMessage.setChatId(chatId);
        sendMessage.setMessageId(messageId);
        InputMediaPhoto photo = new InputMediaPhoto(photoId);
        photo.setParseMode("html");
        photo.setCaption(description);
        sendMessage.setMedia(photo);
        sendMessage.setReplyMarkup(keyboard);
        return sendMessage;
    }

    public static EditMessageText getEditMessageForSeeAds(String chatId, Integer messageId, String text) {
        EditMessageText sendMessage = new EditMessageText();
        sendMessage.setChatId(chatId);
        sendMessage.setMessageId(messageId);
        sendMessage.setText(text);
        return sendMessage;
    }

    public static EditMessageReplyMarkup getEditMessageForSeeAds(String chatId, Integer messageId, InlineKeyboardMarkup keyboard) {
        EditMessageReplyMarkup sendMessage = new EditMessageReplyMarkup();
        sendMessage.setChatId(chatId);
        sendMessage.setMessageId(messageId);
        sendMessage.setReplyMarkup(keyboard);
        return sendMessage;
    }

    public static SendMediaGroup getMessage(String chatId, String description, List<InputMedia> inputMediaPhotos) {
        SendMediaGroup sendMediaGroup = new SendMediaGroup();
        inputMediaPhotos.get(0).setCaption(description);
        sendMediaGroup.setMedias(inputMediaPhotos);
        sendMediaGroup.setChatId(chatId);
        return sendMediaGroup;
    }

    public static SendMediaGroup getMessage(String chatId, List<InputMedia> inputMediaPhotos) {
        SendMediaGroup sendMediaGroup = new SendMediaGroup();
        sendMediaGroup.setMedias(inputMediaPhotos);
        sendMediaGroup.setChatId(chatId);
        return sendMediaGroup;
    }

    public static SendMediaGroup getMessage(String chatId, List<InputMedia> inputMediaPhotos, Integer replyMessageId) {
        SendMediaGroup sendMediaGroup = new SendMediaGroup();
        sendMediaGroup.setMedias(inputMediaPhotos);
        sendMediaGroup.setChatId(chatId);
        sendMediaGroup.setReplyToMessageId(replyMessageId);
        return sendMediaGroup;
    }

    public static SendPhoto getMessage(String chatId, String photoId, Integer replyMessageId) {
        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setPhoto(new InputFile(photoId));
        sendPhoto.setReplyToMessageId(replyMessageId);
        sendPhoto.setChatId(chatId);
        return sendPhoto;
    }
}
