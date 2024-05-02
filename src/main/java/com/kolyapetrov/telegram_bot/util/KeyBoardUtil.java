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

    public static ReplyKeyboardMarkup finishPhotoSending() {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setResizeKeyboard(true);
//        keyboardMarkup.setOneTimeKeyboard(true);
        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row1 = new KeyboardRow();
        KeyboardButton button = new KeyboardButton(FINISH_SENDING_PHOTOS);
        row1.add(button);
        keyboard.add(row1);
        keyboardMarkup.setKeyboard(keyboard);
        return keyboardMarkup;
    }

    public static ReplyKeyboardMarkup choiceLocationOrEnterCity() {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(true);
        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row1 = new KeyboardRow();
        KeyboardButton button1 = new KeyboardButton(SEND_LOCATION);
        button1.setRequestLocation(true);
        row1.add(button1);
        KeyboardRow row2 = new KeyboardRow();
        KeyboardButton button2 = new KeyboardButton(GO_BACK);
        row2.add(button2);
        keyboard.add(row1);
        keyboard.add(row2);
        keyboardMarkup.setKeyboard(keyboard);
        return keyboardMarkup;
    }

    public static ReplyKeyboardMarkup mainKeyBoard() {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setResizeKeyboard(true);
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

    public static InlineKeyboardMarkup seeMyADsKeyboard(Long idOfLeftOrder, Long idOfCurrentOrder, Long idOfRightOrder) {
        InlineKeyboardButton leftOrderButton = getButtonForMyAds(LEFT_AD, idOfLeftOrder);
        InlineKeyboardButton rightOrderButton = getButtonForMyAds(RIGHT_AD, idOfRightOrder);
        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
        keyboardButtonsRow1.add(leftOrderButton);
        keyboardButtonsRow1.add(rightOrderButton);

        InlineKeyboardButton editOrderButton = getButtonForMyAds(EDIT_AD, idOfCurrentOrder);
        InlineKeyboardButton seePhotosOrderButton = getButtonForMyAds(SEE_PHOTOS_AD, idOfCurrentOrder);
        InlineKeyboardButton deleteOrderButton = getButtonForMyAds(DELETE_AD, idOfCurrentOrder);
        List<InlineKeyboardButton> keyboardButtonsRow2 = new ArrayList<>();
        keyboardButtonsRow2.add(seePhotosOrderButton);
        keyboardButtonsRow2.add(editOrderButton);
        keyboardButtonsRow2.add(deleteOrderButton);

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow2);
        rowList.add(keyboardButtonsRow1);

        return new InlineKeyboardMarkup(rowList);
    }

    public static InlineKeyboardMarkup seeMyADsKeyboard(Long idOfCurrentOrder) {
        InlineKeyboardButton editOrderButton = getButtonForMyAds(EDIT_AD, idOfCurrentOrder);
        InlineKeyboardButton seePhotosOrderButton = getButtonForMyAds(SEE_PHOTOS_AD, idOfCurrentOrder);
        InlineKeyboardButton deleteOrderButton = getButtonForMyAds(DELETE_AD, idOfCurrentOrder);
        List<InlineKeyboardButton> keyboardButtonsRow = new ArrayList<>();
        keyboardButtonsRow.add(seePhotosOrderButton);
        keyboardButtonsRow.add(editOrderButton);
        keyboardButtonsRow.add(deleteOrderButton);

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow);

        return new InlineKeyboardMarkup(rowList);
    }

    private static InlineKeyboardButton getButtonForMyAds(String nameOfButton, Long idOfCurrentOrder) {
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(nameOfButton);
        button.setCallbackData(MY_ADS + " " + nameOfButton + " " + idOfCurrentOrder);
        return button;
    }

    public static InlineKeyboardMarkup seeOtherADsKeyboard(Long idOfLeftOrder, Long idOfCurrentOrder, Long idOfRightOrder,
                                                           String city) {
        InlineKeyboardButton leftOrderButton = getButtonForOtherAds(LEFT_AD, idOfLeftOrder, city);
        InlineKeyboardButton rightOrderButton = getButtonForOtherAds(RIGHT_AD, idOfRightOrder, city);
        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
        keyboardButtonsRow1.add(leftOrderButton);
        keyboardButtonsRow1.add(rightOrderButton);

        InlineKeyboardButton seePhotosOrderButton = getButtonForOtherAds(SEE_PHOTOS_AD, idOfCurrentOrder, city);
        List<InlineKeyboardButton> keyboardButtonsRow2 = new ArrayList<>();
        keyboardButtonsRow2.add(seePhotosOrderButton);

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow2);
        rowList.add(keyboardButtonsRow1);

        return new InlineKeyboardMarkup(rowList);
    }

    public static InlineKeyboardMarkup seeOtherADsKeyboard(Long idOfCurrentOrder, String city) {
        InlineKeyboardButton seePhotosOrderButton = getButtonForOtherAds(SEE_PHOTOS_AD, idOfCurrentOrder, city);
        List<InlineKeyboardButton> keyboardButtonsRow = new ArrayList<>();
        keyboardButtonsRow.add(seePhotosOrderButton);

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow);

        return new InlineKeyboardMarkup(rowList);
    }

    private static InlineKeyboardButton getButtonForOtherAds(String nameOfButton, Long idOfCurrentOrder, String city) {
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(nameOfButton);
        button.setCallbackData(OTHER_ADS + " " + nameOfButton + " " + idOfCurrentOrder + " " + city);
        return button;
    }

    public static InlineKeyboardMarkup seeLocalAdsKeyboard(Long idOfLeftOrder, Long idOfCurrentOrder, Long idOfRightOrder,
                                                           Double latitude, Double longitude) {
        InlineKeyboardButton leftOrderButton = getButtonForLocalAds(LEFT_AD, idOfLeftOrder, latitude, longitude);
        InlineKeyboardButton rightOrderButton = getButtonForLocalAds(RIGHT_AD, idOfRightOrder, latitude, longitude);
        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
        keyboardButtonsRow1.add(leftOrderButton);
        keyboardButtonsRow1.add(rightOrderButton);

        InlineKeyboardButton seePhotosOrderButton = getButtonForLocalAds(SEE_PHOTOS_AD, idOfCurrentOrder, latitude, longitude);
        List<InlineKeyboardButton> keyboardButtonsRow2 = new ArrayList<>();
        keyboardButtonsRow2.add(seePhotosOrderButton);

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow2);
        rowList.add(keyboardButtonsRow1);

        return new InlineKeyboardMarkup(rowList);
    }

    public static InlineKeyboardMarkup seeLocalAdsKeyboard(Long idOfCurrentOrder) {
        InlineKeyboardButton seePhotosOrderButton = getButtonForLocalAds(SEE_PHOTOS_AD, idOfCurrentOrder, 0.0, 0.0);
        List<InlineKeyboardButton> keyboardButtonsRow = new ArrayList<>();
        keyboardButtonsRow.add(seePhotosOrderButton);

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow);

        return new InlineKeyboardMarkup(rowList);
    }

    private static InlineKeyboardButton getButtonForLocalAds(String nameOfButton, Long idOfCurrentOrder, Double latitude,
                                                             Double longitude) {
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(nameOfButton);
        button.setCallbackData(LOCAL_ADS + " " + nameOfButton + " " + idOfCurrentOrder + " " + latitude + " " + longitude);
        return button;
    }
}
