package com.kolyapetrov.telegram_bot.controller.callbacks;

import com.kolyapetrov.telegram_bot.controller.CallBackHandler;
import com.kolyapetrov.telegram_bot.model.dto.CallBackInfo;
import com.kolyapetrov.telegram_bot.model.dto.UserInfo;
import com.kolyapetrov.telegram_bot.model.entity.Order;
import com.kolyapetrov.telegram_bot.model.service.LocationService;
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
public class SeeLocalAdsCallBackQuery implements CallBackHandler {
    private final OrderService orderService;
    private final LocationService locationService;

    private final OrdersInMessageService ordersInMessageService;

    public SeeLocalAdsCallBackQuery(OrderService orderService, LocationService locationService,
                                    OrdersInMessageService ordersInMessageService) {
        this.orderService = orderService;
        this.locationService = locationService;
        this.ordersInMessageService = ordersInMessageService;
    }

    @Override
    public CallBackName getCallBack() {
        return CallBackName.LOCAL_ADS;
    }

    @Override
    public void handle(UserInfo userInfo, CallBackInfo callBackInfo, DefaultAbsSender sender) throws TelegramApiException {
        String buttonCallBack = callBackInfo.getNameOfButton();

        /*Order order = orderService.getOrder(callBackInfo.getNumberOfOrder());
        if (order == null) {
            DeleteMessage deleteMessage = new DeleteMessage(userInfo.getChatId(), userInfo.getMessageId());
            sender.execute(deleteMessage);
            sender.execute(MessageUtil.getMessage(userInfo.getChatId(), "Этого объявления больше не существует!"));
            return;
        }*/

        switch (buttonCallBack) {
            case RIGHT_AD, LEFT_AD -> getNextOrderQuery(userInfo, callBackInfo, sender);
            case SEE_PHOTOS_AD -> getOrderPhotosQuery(userInfo, callBackInfo, sender);
        }
    }

    void getNextOrderQuery(UserInfo userInfo, CallBackInfo callBackInfo, DefaultAbsSender sender)
            throws TelegramApiException{

        var ordersDTO = ordersInMessageService
                .findOrderIdsAndDistancesByUserIdAndMessageId(callBackInfo.getMessageId(), userInfo.getAppUser().getUserId());

        List<Order> orders = new ArrayList<>();
        ordersDTO.forEach(orderDistanceDTO -> {
            Order order = orderService.findOrderById(orderDistanceDTO.getOrderId());
            order.setDistance(orderDistanceDTO.getDistance());
            orders.add(order);
        });

        int indexOfCurrentOrder = OrderUtil.getIndexOfOrder(orders, callBackInfo.getNumberOfOrder());
        int[] indexes = OrderUtil.getIndexesOfNeighboringOrders(indexOfCurrentOrder, orders.size());

        Order newCurrentOrder = null;
        if (callBackInfo.getNameOfButton().startsWith(RIGHT_AD)) {
            newCurrentOrder = orders.get(indexes[1]);

        } else if (callBackInfo.getNameOfButton().startsWith(LEFT_AD)) {
            newCurrentOrder = orders.get(indexes[0]);
        }
        String distanceToPerson = OrderUtil.getDistanceToPerson(newCurrentOrder.getDistance());
        String newMainPhotoId = newCurrentOrder.getPhotos().get(0).getId();
        InlineKeyboardMarkup keyboard = KeyBoardUtil.seeLocalAdsKeyboard(newCurrentOrder.getId(), callBackInfo.getMessageId());
        sender.execute(MessageUtil.getEditMessageForSeeAds(userInfo.getChatId(), userInfo.getMessageId(),
                newMainPhotoId, newCurrentOrder + distanceToPerson, keyboard));
    }

    private void getOrderPhotosQuery(UserInfo userInfo, CallBackInfo callBackInfo, DefaultAbsSender sender)
            throws TelegramApiException {

        Order order = orderService.getOrder(callBackInfo.getNumberOfOrder());
        sender.execute(MessageUtil.getAnswerCallbackQuery(callBackInfo.getId(), "Все фото из объявления показаны в " +
                "ответе на текущее сообщение!"));
        var photos = order.getPhotos();
        if (photos.size() == 1) {
            sender.execute(MessageUtil.getMessage(userInfo.getChatId(), photos.get(0).getId(), userInfo.getMessageId()));
        } else {
            List<InputMedia> listPhotos = new ArrayList<>();
            photos.forEach(photo -> listPhotos.add(new InputMediaPhoto(photo.getId())));
            sender.execute(MessageUtil.getMessage(userInfo.getChatId(), listPhotos, userInfo.getMessageId()));
        }
    }
}
