package com.kolyapetrov.telegram_bot.controller.callbacks;

import com.kolyapetrov.telegram_bot.controller.CallBackHandler;
import com.kolyapetrov.telegram_bot.model.dto.CallBackInfo;
import com.kolyapetrov.telegram_bot.model.dto.UserInfo;
import com.kolyapetrov.telegram_bot.model.entity.Order;
import com.kolyapetrov.telegram_bot.model.service.OrderService;
import com.kolyapetrov.telegram_bot.util.MessageUtil;
import com.kolyapetrov.telegram_bot.util.enums.CallBackName;
import com.kolyapetrov.telegram_bot.util.enums.OrderState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.media.InputMedia;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

import static com.kolyapetrov.telegram_bot.util.ConstantMessages.*;

@Component
public class AdminActionsCallBackQuery implements CallBackHandler {
    private final OrderService orderService;

    @Autowired
    public AdminActionsCallBackQuery(OrderService orderService) {
        this.orderService = orderService;
    }

    @Override
    public CallBackName getCallBack() {
        return CallBackName.ADMIN_ACTIONS;
    }

    @Override
    public void handle(UserInfo userInfo, CallBackInfo callBackInfo, DefaultAbsSender sender) throws TelegramApiException {
        String nameOfButton = callBackInfo.getNameOfButton();

        switch (nameOfButton) {
            case SEE_PHOTOS_AD -> seePhotos(userInfo, callBackInfo, sender);
            case DELETE_AD -> deleteOrderQuery(userInfo, callBackInfo, sender);
            case ACCEPT_AD -> acceptOrderQuery(userInfo, callBackInfo, sender);
        }
    }

    private void seePhotos(UserInfo userInfo, CallBackInfo callBackInfo, DefaultAbsSender sender)  throws TelegramApiException {
        Order order = orderService.getOrder(callBackInfo.getNumberOfOrder());

        var photos = order.getPhotos();
        if (photos.size() == 1) {
            sender.execute(MessageUtil.getMessage(userInfo.getChatId(), photos.get(0).getId(), userInfo.getMessageId()));
        } else {
            List<InputMedia> listPhotos = new ArrayList<>();
            photos.forEach(photo -> listPhotos.add(new InputMediaPhoto(photo.getId())));
            sender.execute(MessageUtil.getMessage(userInfo.getChatId(), listPhotos, userInfo.getMessageId()));
        }
    }

    private void deleteOrderQuery(UserInfo userInfo, CallBackInfo callBackInfo, DefaultAbsSender sender) throws TelegramApiException {
        Order order = orderService.findOrderById(callBackInfo.getNumberOfOrder());
        order.setState(OrderState.FORBIDDEN);
        orderService.saveOrder(order);
        sender.execute(MessageUtil.getAnswerCallbackQuery(callBackInfo.getId(),"Объявление отклонено!"));
        DeleteMessage deleteMessage = DeleteMessage.builder()
                .chatId(userInfo.getChatId())
                .messageId(userInfo.getMessageId())
                .build();
        sender.execute(deleteMessage);
    }

    private void acceptOrderQuery(UserInfo userInfo, CallBackInfo callBackInfo, DefaultAbsSender sender) throws TelegramApiException {
        Order order = orderService.findOrderById(callBackInfo.getNumberOfOrder());
        order.setState(OrderState.CHECKED);
        orderService.saveOrder(order);
        sender.execute(MessageUtil.getAnswerCallbackQuery(callBackInfo.getId(), "Объявление принято!"));
        DeleteMessage deleteMessage = DeleteMessage.builder()
                .chatId(userInfo.getChatId())
                .messageId(userInfo.getMessageId())
                .build();
        sender.execute(deleteMessage);
    }
}
