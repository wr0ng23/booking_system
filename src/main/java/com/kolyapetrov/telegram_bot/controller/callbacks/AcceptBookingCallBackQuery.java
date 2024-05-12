package com.kolyapetrov.telegram_bot.controller.callbacks;

import com.kolyapetrov.telegram_bot.controller.CallBackHandler;
import com.kolyapetrov.telegram_bot.model.dto.CallBackInfo;
import com.kolyapetrov.telegram_bot.model.dto.UserInfo;
import com.kolyapetrov.telegram_bot.model.entity.Booking;
import com.kolyapetrov.telegram_bot.model.entity.BookingTemp;
import com.kolyapetrov.telegram_bot.model.entity.Order;
import com.kolyapetrov.telegram_bot.model.service.BookingService;
import com.kolyapetrov.telegram_bot.model.service.BookingTempTableService;
import com.kolyapetrov.telegram_bot.model.service.OrderService;
import com.kolyapetrov.telegram_bot.util.KeyBoardUtil;
import com.kolyapetrov.telegram_bot.util.MessageUtil;
import com.kolyapetrov.telegram_bot.util.enums.CallBackName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDate;

@Component
public class AcceptBookingCallBackQuery implements CallBackHandler {
    private final BookingService bookingService;
    private final BookingTempTableService bookingTempTableService;
    private final OrderService orderService;

    @Autowired
    public AcceptBookingCallBackQuery(BookingService bookingService, BookingTempTableService bookingTempTableService,
                                      OrderService orderService) {
        this.bookingService = bookingService;
        this.bookingTempTableService = bookingTempTableService;
        this.orderService = orderService;
    }

    @Override
    public CallBackName getCallBack() {
        return CallBackName.ACCEPT_BOOKING;
    }

    @Override
    public void handle(UserInfo userInfo, CallBackInfo callBackInfo, DefaultAbsSender sender) throws TelegramApiException {
        Long userId = userInfo.getAppUser().getUserId();
        Long orderId = callBackInfo.getNumberOfOrder();

        bookingTempTableService.createTempTable();
        var tempBookingRecordId = bookingTempTableService.getRecordId(userId, orderId);
        BookingTemp bookingTemp = bookingTempTableService.getRecordById(tempBookingRecordId);
        if (bookingTemp == null || bookingTemp.getStartDate() == null || bookingTemp.getEndDate() == null) {
            sender.execute(MessageUtil.getAnswerCallbackQuery(callBackInfo.getId(),
                    "Вам необходимо выбрать дату начала бронирования и конца!"));
            return;
        }

        try {
            Booking booking = bookingService.insertNewRecord(bookingTemp);
            bookingTempTableService.deleteRecordById(tempBookingRecordId);
            DeleteMessage deleteMessage = new DeleteMessage(userInfo.getChatId(), userInfo.getMessageId());
            sender.execute(deleteMessage);

            String username = orderService.findUserNameByOrderId(orderId);
            sender.execute(MessageUtil.getMessage(userInfo.getChatId(), "Объявление забронировано, " +
                    "вы можете связаться с арендодателем для уточнения деталей!\n tg арендодателя: @" + username));

            String landLordId = orderService.findUserIdByOrderId(orderId).toString();
            LocalDate startDate = bookingTemp.getStartDate();
            LocalDate endDate = bookingTemp.getEndDate();
            Order order = orderService.findOrderById(orderId);
            long price = order.getPrice() * startDate.datesUntil(endDate).toList().size();

            sender.execute(MessageUtil.getMessage(landLordId, "Пользователь: @" + userInfo.getAppUser().getNameOfUser()
                            + " забронировал у вас жилье: \"" + order.getTitle() + "\" на период с " + startDate + " до " + endDate +
                            " на итоговую сумму: " + price + "руб. (" + order.getPrice() + "руб./сутки)",
                    KeyBoardUtil.acceptBookingKeyboard(booking.getId())));

        } catch (Exception e) {
            Booking booking = bookingService.findBooking(bookingTemp);
            sender.execute(MessageUtil.getAnswerCallbackQuery(callBackInfo.getId(),
                    "Вы не можете забронировать эти даты. " + booking.getStatus().getName() + "!"));
        }
    }
}
