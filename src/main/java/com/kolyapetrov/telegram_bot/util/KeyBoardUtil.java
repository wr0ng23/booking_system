package com.kolyapetrov.telegram_bot.util;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

import static com.kolyapetrov.telegram_bot.util.ConstantMessages.*;

public class KeyBoardUtil {
    private KeyBoardUtil() {
    }

    public static ReplyKeyboardMarkup locationKeyBoard() {
        return getReplyKeyboardMarkup("Отправить геопозицию", true);
    }

    private static ReplyKeyboardMarkup getReplyKeyboardMarkup(String name, boolean location) {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setResizeKeyboard(true);
//        keyboardMarkup.setOneTimeKeyboard(true);
        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row1 = new KeyboardRow();
        KeyboardButton button = new KeyboardButton(name);
        if (location) button.setRequestLocation(true);
        row1.add(button);
        keyboard.add(row1);
        keyboardMarkup.setKeyboard(keyboard);
        return keyboardMarkup;
    }

    public static ReplyKeyboardMarkup finishPhotoSending() {
        return getReplyKeyboardMarkup(FINISH_SENDING_PHOTOS, false);
    }

    public static ReplyKeyboardMarkup mainKeyBoard() {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setResizeKeyboard(true);
//        keyboardMarkup.setOneTimeKeyboard(true);
        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row1 = new KeyboardRow();
        KeyboardRow row2 = new KeyboardRow();
        KeyboardRow row3 = new KeyboardRow();
        row1.add(CREATE_NEW_ADVERTISEMENT);
        row2.add(SEE_MY_ADVERTISEMENTS);
        row3.add(SEARCH_BY_ADVERTISEMENTS);
        keyboard.add(row1);
        keyboard.add(row2);
        keyboard.add(row3);
        keyboardMarkup.setKeyboard(keyboard);
        return keyboardMarkup;
    }

    public static InlineKeyboardMarkup seeADsKeyboard(String idOfLeftOrder, String idOfCurrentOrder, String idOfRightOrder) {
        InlineKeyboardButton leftOrderButton = getButton(LEFT_AD, idOfLeftOrder);
        InlineKeyboardButton rightOrderButton = getButton(RIGHT_AD, idOfRightOrder);
        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
        keyboardButtonsRow1.add(leftOrderButton);
        keyboardButtonsRow1.add(rightOrderButton);

        InlineKeyboardButton editOrderButton = getButton(EDIT_AD, idOfCurrentOrder);
        InlineKeyboardButton seePhotosOrderButton = getButton(SEE_PHOTOS_AD, idOfCurrentOrder);
        InlineKeyboardButton deleteOrderButton = getButton(DELETE_AD, idOfCurrentOrder);
        List<InlineKeyboardButton> keyboardButtonsRow2 = new ArrayList<>();
        keyboardButtonsRow2.add(seePhotosOrderButton);
        keyboardButtonsRow2.add(editOrderButton);
        keyboardButtonsRow2.add(deleteOrderButton);

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow2);
        rowList.add(keyboardButtonsRow1);

        return new InlineKeyboardMarkup(rowList);
    }

    private static InlineKeyboardButton getButton(String nameOfButton, String idOfCurrentOrder) {
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(nameOfButton);
        button.setCallbackData(nameOfButton + " " + idOfCurrentOrder);
        return button;
    }
}
