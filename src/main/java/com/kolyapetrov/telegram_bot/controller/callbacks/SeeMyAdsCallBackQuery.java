package com.kolyapetrov.telegram_bot.controller.callbacks;

import com.kolyapetrov.telegram_bot.controller.CallBackHandler;
import com.kolyapetrov.telegram_bot.model.dto.CallBackInfo;
import com.kolyapetrov.telegram_bot.model.dto.UserInfo;
import com.kolyapetrov.telegram_bot.model.entity.AppUser;
import com.kolyapetrov.telegram_bot.model.entity.Order;
import com.kolyapetrov.telegram_bot.model.service.OrderService;
import com.kolyapetrov.telegram_bot.model.service.UserService;
import com.kolyapetrov.telegram_bot.util.*;
import com.kolyapetrov.telegram_bot.util.enums.CallBackName;
import com.kolyapetrov.telegram_bot.util.enums.UserState;
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
public class SeeMyAdsCallBackQuery implements CallBackHandler {
    private final UserService userService;
    private final OrderService orderService;

    @Autowired
    public SeeMyAdsCallBackQuery(UserService userService, OrderService orderService) {
        this.userService = userService;
        this.orderService = orderService;
    }

    @Override
    public CallBackName getCallBack() {
        return CallBackName.MY_ADS;
    }

    @Override
    public void handle(UserInfo userInfo, CallBackInfo callBackInfo, DefaultAbsSender sender) throws TelegramApiException{
        String nameOfButton = callBackInfo.getNameOfButton();
        Long numberOfOrder = callBackInfo.getNumberOfOrder();

        Order order = orderService.getOrder(numberOfOrder);
        if (order == null) {
            DeleteMessage deleteMessage = new DeleteMessage(userInfo.getChatId(), userInfo.getMessageId());
            sender.execute(deleteMessage);
            sender.execute(MessageUtil.getMessage(userInfo.getChatId(), "Этого объявления больше не существует!"));
            return;
        }

        switch (nameOfButton) {
            case RIGHT_AD, LEFT_AD -> getNextOrderQuery(userInfo, callBackInfo, sender);
            case SEE_PHOTOS_AD -> getOrderPhotosQuery(userInfo, callBackInfo, sender);
            case DELETE_AD -> deleteOrderQuery(userInfo, callBackInfo, sender);
            case EDIT_AD -> editOrderQuery(userInfo, callBackInfo, sender);
        }
    }

    private void editOrderQuery(UserInfo userInfo, CallBackInfo callBackInfo, DefaultAbsSender sender) throws TelegramApiException {
        userInfo.getAppUser().setUserState(UserState.EDIT_AD);
        var orders = getUserOrders(userInfo.getAppUser());
        var order = orders.get(OrderUtil.getIndexOfOrder(orders, callBackInfo.getNumberOfOrder()));
        order.setEditing(true);
        userService.saveUser(userInfo.getAppUser());
        sender.execute(MessageUtil.getMessage(userInfo.getChatId(), "Введите новое описание для объявления: "));
    }

    private void deleteOrderQuery(UserInfo userInfo, CallBackInfo callBackInfo, DefaultAbsSender sender) throws TelegramApiException {
        AppUser appUser = userInfo.getAppUser();
        List<Order> orders = getUserOrders(appUser);
        int index = OrderUtil.getIndexOfOrder(orders, callBackInfo.getNumberOfOrder());
        orderService.deleteOrder(orders.get(index));

        // bad code, but it still works
        if (orders.size() == 1) {
            sender.execute(new DeleteMessage(userInfo.getChatId(), userInfo.getMessageId()));
            sender.execute(MessageUtil.getAnswerCallbackQuery(callBackInfo.getId(),
                    "Объявление успешно удалено. У вас больше нет созданных объявлений!"));

        } else {
            int[] indexes = OrderUtil.getIndexesOfNeighboringOrders(index, orders.size());
            callBackInfo.setNumberOfOrder(orders.get(indexes[1]).getId());

            orders.remove(index);
            appUser.setOrders(orders);
            userService.saveUser(appUser);
            sender.execute(MessageUtil.getAnswerCallbackQuery(callBackInfo.getId(),
                    "Объявление успешно удалено!"));
            getNextOrderQuery(userInfo, callBackInfo, sender);
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

    private void getNextOrderQuery(UserInfo userInfo, CallBackInfo callBackInfo, DefaultAbsSender sender) throws TelegramApiException {
        List<Order> userOrders = getUserOrders(userInfo.getAppUser());

        int indexOfCurrentOrderInOrderList = OrderUtil.getIndexOfOrder(userOrders, callBackInfo.getNumberOfOrder());
        Order newCurrentOrder = userOrders.get(indexOfCurrentOrderInOrderList);
        String newMainPhotoId = newCurrentOrder.getPhotos().get(0).getId();

        if (userOrders.size() > 1) {
            int[] indexes = OrderUtil.getIndexesOfNeighboringOrders(indexOfCurrentOrderInOrderList, userOrders.size());
            Order leftNewOrder = userOrders.get(indexes[0]);
            Order rightNewOrder = userOrders.get(indexes[1]);

            InlineKeyboardMarkup keyboard = KeyBoardUtil.seeMyADsKeyboard(leftNewOrder.getId(),
                    newCurrentOrder.getId(), rightNewOrder.getId());

            sender.execute(MessageUtil.getEditMessageForSeeAds(userInfo.getChatId(), userInfo.getMessageId(),
                    newMainPhotoId, newCurrentOrder.toStringMyAd(), keyboard));
        } else {
            InlineKeyboardMarkup keyboard = KeyBoardUtil.seeMyADsKeyboard(newCurrentOrder.getId());
            sender.execute(MessageUtil.getEditMessageForSeeAds(userInfo.getChatId(), userInfo.getMessageId(),
                    newMainPhotoId, newCurrentOrder.toStringMyAd(), keyboard));
        }

    }

    private List<Order> getUserOrders(AppUser appUser) {
        return new ArrayList<>(appUser.getOrders()
                .stream()
                .sorted(Comparator.comparing(Order::getId))
                .toList());
    }
}
