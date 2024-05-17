package com.kolyapetrov.telegram_bot.controller.callbacks;

import com.kolyapetrov.telegram_bot.controller.CallBackHandler;
import com.kolyapetrov.telegram_bot.model.dto.CallBackInfo;
import com.kolyapetrov.telegram_bot.model.dto.UserInfo;
import com.kolyapetrov.telegram_bot.model.entity.Order;
import com.kolyapetrov.telegram_bot.model.service.OrderService;
import com.kolyapetrov.telegram_bot.model.service.OrdersInMessageService;
import com.kolyapetrov.telegram_bot.util.enums.CallBackName;
import com.kolyapetrov.telegram_bot.util.KeyBoardUtil;
import com.kolyapetrov.telegram_bot.util.MessageUtil;
import com.kolyapetrov.telegram_bot.util.OrderUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.media.InputMedia;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static com.kolyapetrov.telegram_bot.util.ConstantMessages.*;

@Component
public class SeeOtherAdsCallBackQuery implements CallBackHandler {
    private final OrderService orderService;
    private final OrdersInMessageService ordersInMessageService;

    public SeeOtherAdsCallBackQuery(OrderService orderService, OrdersInMessageService ordersInMessageService) {
        this.orderService = orderService;
        this.ordersInMessageService = ordersInMessageService;
    }

    @Override
    public void handle(UserInfo userInfo, CallBackInfo callBackInfo, DefaultAbsSender sender)
            throws TelegramApiException {
        String buttonCallBack = callBackInfo.getNameOfButton();

        /*Order order = orderService.getOrder(callBackInfo.getNumberOfOrder());
        if (order == null) {
            DeleteMessage deleteMessage = new DeleteMessage(userInfo.getChatId(), userInfo.getMessageId());
            sender.execute(deleteMessage);
            sender.execute(MessageUtil.getMessage(userInfo.getChatId(), "Этого объявления больше не существует!"));
            return;
        }
*/
        switch (buttonCallBack) {
            case RIGHT_AD, LEFT_AD -> getNextOrderQuery(userInfo, callBackInfo, sender);
            case SEE_PHOTOS_AD -> getOrderPhotosQuery(userInfo, callBackInfo, sender);
        }
    }

    private void getOrderPhotosQuery(UserInfo userInfo, CallBackInfo callBackInfo, DefaultAbsSender sender) throws TelegramApiException {
        Order order = orderService.getOrder(callBackInfo.getNumberOfOrder());

        var photos = order.getPhotos();
        sender.execute(MessageUtil.getAnswerCallbackQuery(callBackInfo.getId(), "Все фото из объявления показаны в " +
                "ответе на текущее сообщение!"));

        if (photos.size() == 1) {
            sender.execute(MessageUtil.getMessage(userInfo.getChatId(), photos.get(0).getId(), userInfo.getMessageId()));
        } else {
            List<InputMedia> listPhotos = new ArrayList<>();
            photos.forEach(photo -> listPhotos.add(new InputMediaPhoto(photo.getId())));
            sender.execute(MessageUtil.getMessage(userInfo.getChatId(), listPhotos, userInfo.getMessageId()));
        }
    }

    void getNextOrderQuery(UserInfo userInfo, CallBackInfo callBackInfo, DefaultAbsSender sender) throws TelegramApiException {
        List<Order> orders = getUserOrders(callBackInfo.getMessageId(), userInfo.getAppUser().getUserId());

        int indexOfCurrentOrder = OrderUtil.getIndexOfOrder(orders, callBackInfo.getNumberOfOrder());
        int[] indexes = OrderUtil.getIndexesOfNeighboringOrders(indexOfCurrentOrder, orders.size());

        Order newCurrentOrder = null;
        if (callBackInfo.getNameOfButton().equals(RIGHT_AD)) {
            newCurrentOrder = orders.get(indexes[1]);
        } else if (callBackInfo.getNameOfButton().equals(LEFT_AD)) {
            newCurrentOrder = orders.get(indexes[0]);
        } else {
            throw new RuntimeException("Incorrect name of button in SeeOtherAdsCallBackQuery!");
        }

        String newMainPhotoId = newCurrentOrder.getPhotos().get(0).getId();
        Long messageId = callBackInfo.getMessageId();
        InlineKeyboardMarkup keyboard = KeyBoardUtil.seeOtherADsKeyboard(newCurrentOrder.getId(), messageId);

        sender.execute(MessageUtil.getEditMessageForSeeAds(userInfo.getChatId(), userInfo.getMessageId(),
                newMainPhotoId, newCurrentOrder.toString(), keyboard));
    }

    private List<Order> getUserOrders(Long messageId, Long userId) {
        return ordersInMessageService.findOrdersByMessageIdAndUserId(messageId, userId)
                .stream()
                .sorted(Comparator.comparing(Order::getId))
                .toList();
    }

    @Override
    public CallBackName getCallBack() {
        return CallBackName.OTHER_ADS;
    }
}
