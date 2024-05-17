package com.kolyapetrov.telegram_bot.util;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static com.kolyapetrov.telegram_bot.util.ConstantMessages.*;

public class KeyBoardUtil {
    private KeyBoardUtil() {
    }

    public static ReplyKeyboardMarkup finishPhotoSending() {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(true);
        keyboardMarkup.setIsPersistent(false);
        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row1 = new KeyboardRow();
        KeyboardButton button = new KeyboardButton(FINISH_SENDING_PHOTOS);
        row1.add(button);
        keyboard.add(row1);
        keyboardMarkup.setKeyboard(keyboard);
        return keyboardMarkup;
    }

    public static InlineKeyboardMarkup filterForAds(long idOfFilter) {
        String[] buttonLabels = {ENTER_CITY, ENTER_LOWER_PRICE, ENTER_UPPER_PRICE, ENTER_LOWER_DATE, ENTER_UPPER_DATE,
                SEND_LOCATION, SEARCH};
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();

        for (String label : buttonLabels) {
            InlineKeyboardButton button = getButton(SEARCH_FILTER, label, idOfFilter);
            List<InlineKeyboardButton> keyboardButtonsRow = new ArrayList<>();
            keyboardButtonsRow.add(button);
            rowList.add(keyboardButtonsRow);
        }

        return new InlineKeyboardMarkup(rowList);
    }

    public static InlineKeyboardMarkup cancelFilter() {
        InlineKeyboardButton button = getButton(CANCEL_FILTER, CANCEL_ACTION, 0L);
        List<InlineKeyboardButton> keyboardButtonsRow = new ArrayList<>();
        keyboardButtonsRow.add(button);

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow);

        return new InlineKeyboardMarkup(rowList);
    }

    public static ReplyKeyboardMarkup choiceLocationOrEnterCity() {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(true);
        keyboardMarkup.setIsPersistent(false);
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

    public static InlineKeyboardMarkup acceptBookingKeyboard(Long bookingId) {
        InlineKeyboardButton acceptBookingButton = getButton(BOOKING_REQUEST, ACCEPT_AD, bookingId);
        InlineKeyboardButton deleteBookingButton = getButton(BOOKING_REQUEST, DELETE_AD, bookingId);
        List<InlineKeyboardButton> keyboardButtonsRow = new ArrayList<>();
        keyboardButtonsRow.add(acceptBookingButton);
        keyboardButtonsRow.add(deleteBookingButton);

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow);

        return new InlineKeyboardMarkup(rowList);
    }


    public static InlineKeyboardMarkup seeMyADsKeyboard(Long idOfCurrentOrder) {
        InlineKeyboardButton leftOrderButton = getButton(MY_ADS, LEFT_AD, idOfCurrentOrder);
        InlineKeyboardButton rightOrderButton = getButton(MY_ADS, RIGHT_AD, idOfCurrentOrder);
        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
        keyboardButtonsRow1.add(leftOrderButton);
        keyboardButtonsRow1.add(rightOrderButton);

        InlineKeyboardButton editOrderButton = getButton(MY_ADS, EDIT_AD, idOfCurrentOrder);
        InlineKeyboardButton seePhotosOrderButton = getButton(MY_ADS, SEE_PHOTOS_AD, idOfCurrentOrder);
        InlineKeyboardButton deleteOrderButton = getButton(MY_ADS, DELETE_AD, idOfCurrentOrder);
        List<InlineKeyboardButton> keyboardButtonsRow2 = new ArrayList<>();
        keyboardButtonsRow2.add(seePhotosOrderButton);
        keyboardButtonsRow2.add(editOrderButton);
        keyboardButtonsRow2.add(deleteOrderButton);

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow2);
        rowList.add(keyboardButtonsRow1);

        return new InlineKeyboardMarkup(rowList);
    }

    public static InlineKeyboardMarkup adminKeyboard(Long idOfCurrentOrder) {
        InlineKeyboardButton seePhotosOrderButton = getButton(ADMIN_ADS, SEE_PHOTOS_AD, idOfCurrentOrder);
        InlineKeyboardButton acceptOrderButton = getButton(ADMIN_ADS, ACCEPT_AD, idOfCurrentOrder);
        InlineKeyboardButton deleteOrderButton = getButton(ADMIN_ADS, DELETE_AD, idOfCurrentOrder);
        List<InlineKeyboardButton> keyboardButtonsRow = new ArrayList<>();
        keyboardButtonsRow.add(seePhotosOrderButton);
        keyboardButtonsRow.add(acceptOrderButton);
        keyboardButtonsRow.add(deleteOrderButton);

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow);

        return new InlineKeyboardMarkup(rowList);
    }

    public static InlineKeyboardMarkup seeMyADsKeyboard2(Long idOfCurrentOrder) {
        InlineKeyboardButton editOrderButton = getButton(MY_ADS, EDIT_AD, idOfCurrentOrder);
        InlineKeyboardButton seePhotosOrderButton = getButton(MY_ADS, SEE_PHOTOS_AD, idOfCurrentOrder);
        InlineKeyboardButton deleteOrderButton = getButton(MY_ADS, DELETE_AD, idOfCurrentOrder);
        List<InlineKeyboardButton> keyboardButtonsRow = new ArrayList<>();
        keyboardButtonsRow.add(seePhotosOrderButton);
        keyboardButtonsRow.add(editOrderButton);
        keyboardButtonsRow.add(deleteOrderButton);

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow);

        return new InlineKeyboardMarkup(rowList);
    }

    public static InlineKeyboardMarkup seeOtherADsKeyboard(Long idOfCurrentOrder, Long idOfMessage) {
        InlineKeyboardButton leftOrderButton = getButton(OTHER_ADS, LEFT_AD, idOfCurrentOrder, idOfMessage);
        InlineKeyboardButton rightOrderButton = getButton(OTHER_ADS, RIGHT_AD, idOfCurrentOrder, idOfMessage);
        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
        keyboardButtonsRow1.add(leftOrderButton);
        keyboardButtonsRow1.add(rightOrderButton);

        InlineKeyboardButton seePhotosOrderButton = getButton(OTHER_ADS, SEE_PHOTOS_AD, idOfCurrentOrder, idOfMessage);
        InlineKeyboardButton bookAdButton = getButton(BOOKING_PRIVATE, BOOKING, idOfCurrentOrder, idOfMessage);
        List<InlineKeyboardButton> keyboardButtonsRow2 = new ArrayList<>();
        keyboardButtonsRow2.add(seePhotosOrderButton);
        keyboardButtonsRow2.add(bookAdButton);

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow2);
        rowList.add(keyboardButtonsRow1);

        return new InlineKeyboardMarkup(rowList);
    }

    public static InlineKeyboardMarkup seeOtherADsKeyboard2(Long idOfCurrentOrder, Long idOfMessage) {
        InlineKeyboardButton seePhotosOrderButton = getButton(OTHER_ADS, SEE_PHOTOS_AD, idOfCurrentOrder, idOfMessage);
        InlineKeyboardButton bookAdButton = getButton(BOOKING_PRIVATE, BOOKING, idOfCurrentOrder, idOfMessage);
        List<InlineKeyboardButton> keyboardButtonsRow = new ArrayList<>();
        keyboardButtonsRow.add(seePhotosOrderButton);
        keyboardButtonsRow.add(bookAdButton);

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow);

        return new InlineKeyboardMarkup(rowList);
    }

    public static InlineKeyboardMarkup seeLocalAdsKeyboard(Long idOfCurrentOrder, Long idOfMessage) {
        InlineKeyboardButton leftOrderButton = getButton(LOCAL_ADS, LEFT_AD, idOfCurrentOrder, idOfMessage);
        InlineKeyboardButton rightOrderButton = getButton(LOCAL_ADS, RIGHT_AD, idOfCurrentOrder, idOfMessage);
        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
        keyboardButtonsRow1.add(leftOrderButton);
        keyboardButtonsRow1.add(rightOrderButton);

        InlineKeyboardButton seePhotosOrderButton = getButton(LOCAL_ADS, SEE_PHOTOS_AD, idOfCurrentOrder, idOfMessage);
        InlineKeyboardButton bookAdButton = getButton(BOOKING_PRIVATE, BOOKING, idOfCurrentOrder);

        List<InlineKeyboardButton> keyboardButtonsRow2 = new ArrayList<>();
        keyboardButtonsRow2.add(seePhotosOrderButton);
        keyboardButtonsRow2.add(bookAdButton);

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow2);
        rowList.add(keyboardButtonsRow1);

        return new InlineKeyboardMarkup(rowList);
    }

    public static InlineKeyboardMarkup seeLocalAdsKeyboard2(Long idOfCurrentOrder, Long idOfMessage) {
        InlineKeyboardButton seePhotosOrderButton = getButton(LOCAL_ADS, SEE_PHOTOS_AD, idOfCurrentOrder, idOfMessage);
        InlineKeyboardButton bookAdButton = getButton(BOOKING_PRIVATE, BOOKING, idOfCurrentOrder);
        List<InlineKeyboardButton> keyboardButtonsRow = new ArrayList<>();
        keyboardButtonsRow.add(seePhotosOrderButton);
        keyboardButtonsRow.add(bookAdButton);

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow);

        return new InlineKeyboardMarkup(rowList);
    }

    private static InlineKeyboardButton getButton(String typeOfButton, String nameOfButton, Long idOfCurrentOrder,
                                                  Long messageId) {
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(nameOfButton);
        button.setCallbackData(typeOfButton + " " + nameOfButton + " " + idOfCurrentOrder + " " + messageId);
        return button;
    }

    private static InlineKeyboardButton getButton(String typeOfButton, String nameOfButton, Long id) {
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(nameOfButton);
        button.setCallbackData(typeOfButton + " " + nameOfButton + " " + id);
        return button;
    }

    public static InlineKeyboardMarkup getKeyboardForDates(Long numberOfOrder, List<LocalDate> bookedDates,
                                                           LocalDate startBooking, LocalDate endBooking) {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        keyboard.add(new ArrayList<>());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM");

        int columns = 3;
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.of(LocalDate.now().getYear(), LocalDate.now().getMonth(), 1)
                .plusMonths(1)
                .minusDays(1);

        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            InlineKeyboardButton button;
            if (!bookedDates.contains(date)) {
                String buttonText = date.format(formatter);
                String callbackData = SELECT_DATE + " " + date + " " + numberOfOrder;
                button = InlineKeyboardButton.builder()
                        .text(buttonText)
                        .callbackData(callbackData)
                        .build();

                if (date.equals(startBooking)) {
                    button.setText("\uD83D\uDCC5 " + buttonText);
                    button.setCallbackData(ALREADY_SELECTED + " " + date + " " + numberOfOrder);
                }

                if (date.equals(endBooking)) {
                    button.setText(button.getText() + " \uD83C\uDFC1");
                    button.setCallbackData(ALREADY_SELECTED + " " + date + " " + numberOfOrder);
                }

            } else {
                String callbackData = ALREADY_BOOKED + " " + date + " " + numberOfOrder;
                button = InlineKeyboardButton.builder()
                        .text("\uD83D\uDD12" + date.format(formatter))
                        .callbackData(callbackData)
                        .build();
            }

            keyboard.get(keyboard.size() - 1).add(button);
            if (keyboard.get(keyboard.size() - 1).size() == columns) {
                keyboard.add(new ArrayList<>());
            }
        }
        List<InlineKeyboardButton> inlineKeyboardButtons = new ArrayList<>();
        InlineKeyboardButton button = InlineKeyboardButton.builder()
                .text(ACCEPT_BOOKING)
                .callbackData(ACCEPT_BOOKING_PRIVATE + " " + numberOfOrder)
                .build();

        inlineKeyboardButtons.add(button);
        keyboard.add(inlineKeyboardButtons);
        markup.setKeyboard(keyboard);
        return markup;
    }

    public static InlineKeyboardMarkup getKeyBoardCalendarForSearching(LocalDate startDate, LocalDate endDate) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        LocalDate currentDate = LocalDate.now();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM");

        for (int i = 0; i < 30; i++) {
            LocalDate date = currentDate.plusDays(i);
            String dateText = date.format(formatter);

            int rowIdx = i / 3;
            int colIdx = i % 3;

            if (colIdx == 0) {
                keyboard.add(new ArrayList<>());
            }

            InlineKeyboardButton button = new InlineKeyboardButton();
            if (startDate != null && date.isEqual(startDate)) {
                button.setText("\uD83D\uDCC5 " + dateText);
            } else if (endDate != null && date.isEqual(endDate)) {
                button.setText(dateText + " \uD83C\uDFC1");
            } else {
                button.setText(dateText);
            }
            button.setCallbackData(SELECT_DATE_FOR_FILTER + " " + date);

            keyboard.get(rowIdx).add(button);
        }

        inlineKeyboardMarkup.setKeyboard(keyboard);

        InlineKeyboardButton button = getButton(CANCEL_FILTER, CANCEL_ACTION, 0L);
        keyboard.add(new ArrayList<>());
        keyboard.get(keyboard.size() - 1).add(button);

        return inlineKeyboardMarkup;
    }
}
