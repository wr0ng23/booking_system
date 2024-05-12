package com.kolyapetrov.telegram_bot.controller.callbacks;

import com.kolyapetrov.telegram_bot.controller.CallBackHandler;
import com.kolyapetrov.telegram_bot.model.dto.CallBackInfo;
import com.kolyapetrov.telegram_bot.model.dto.UserInfo;
import com.kolyapetrov.telegram_bot.model.entity.Booking;
import com.kolyapetrov.telegram_bot.model.service.BookingService;
import com.kolyapetrov.telegram_bot.util.MessageUtil;
import com.kolyapetrov.telegram_bot.util.enums.BookingStatus;
import com.kolyapetrov.telegram_bot.util.enums.CallBackName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import static com.kolyapetrov.telegram_bot.util.ConstantMessages.ACCEPT_AD;
import static com.kolyapetrov.telegram_bot.util.ConstantMessages.DELETE_AD;

@Component
public class BookingRequestCallBackQuery implements CallBackHandler {
    private final BookingService bookingService;

    @Autowired
    public BookingRequestCallBackQuery(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @Override
    public CallBackName getCallBack() {
        return CallBackName.BOOKING_REQUEST;
    }

    @Override
    public void handle(UserInfo userInfo, CallBackInfo callBackInfo, DefaultAbsSender sender) throws TelegramApiException {
        String nameOfButton = callBackInfo.getNameOfButton();

        switch (nameOfButton) {
            case ACCEPT_AD -> acceptBookingQuery(userInfo, callBackInfo, sender);
            case DELETE_AD -> rejectBookingQuery(userInfo, callBackInfo, sender);
        }
    }

    private void rejectBookingQuery(UserInfo userInfo, CallBackInfo callBackInfo, DefaultAbsSender sender) throws TelegramApiException {
        Long idOfBooking = callBackInfo.getNumberOfOrder();
        Booking booking = bookingService.findBookingById(idOfBooking);
        Long userId = booking.getUser().getUserId();

        booking.setStatus(BookingStatus.FORBIDDEN);
        bookingService.saveBooking(booking);

        DeleteMessage deleteMessage = new DeleteMessage(userInfo.getChatId(), userInfo.getMessageId());
        sender.executeAsync(deleteMessage);

        sender.execute(MessageUtil.getAnswerCallbackQuery(callBackInfo.getId(), "Вы отказали в бронировании. " +
                "Пользователь получит уведомление!"));
        sender.execute(MessageUtil.getMessage(userId.toString(), "Арендодатель: @" + userInfo.getAppUser().getNameOfUser() +
                " отказал вам на запрос о бронирование объявления - \"" + booking.getOrder().getTitle() +
                "\" на период с " + booking.getDateStart() + " по " + booking.getDateEnd() + "."));
    }

    private void acceptBookingQuery(UserInfo userInfo, CallBackInfo callBackInfo, DefaultAbsSender sender) throws TelegramApiException {
        Long idOfBooking = callBackInfo.getNumberOfOrder();
        Booking booking = bookingService.findBookingById(idOfBooking);
        booking.setStatus(BookingStatus.ACCEPT);
        bookingService.saveBooking(booking);

        DeleteMessage deleteMessage = new DeleteMessage(userInfo.getChatId(), userInfo.getMessageId());
        sender.execute(deleteMessage);

        sender.execute(MessageUtil.getAnswerCallbackQuery(callBackInfo.getId(), "Пользователь получит уведомление " +
                "об успешном бронировании на выбранные даты!"));

        Long userId = booking.getUser().getUserId();
        sender.execute(MessageUtil.getMessage(userId.toString(), "Арендодатель: @" + userInfo.getAppUser().getNameOfUser() +
                " дал согласие по поводу ващего запроса на бронирование объявления - \"" + booking.getOrder().getTitle() +
                "\" на период с " + booking.getDateStart() + " по " + booking.getDateEnd() + "."));
    }
}
