package com.kolyapetrov.telegram_bot.controller.actions;

import com.kolyapetrov.telegram_bot.model.dto.UserInfo;
import com.kolyapetrov.telegram_bot.model.entity.Order;
import com.kolyapetrov.telegram_bot.model.service.OrderService;
import com.kolyapetrov.telegram_bot.util.KeyBoardUtil;
import com.kolyapetrov.telegram_bot.util.MessageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.meta.api.objects.media.InputMedia;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static com.kolyapetrov.telegram_bot.util.ConstantMessages.*;

@Component
public class SeeOtherAdsCallBackQuery {
    private final OrderService orderService;

    @Autowired
    public SeeOtherAdsCallBackQuery(OrderService orderService) {
        this.orderService = orderService;
    }

    public void handle(UserInfo userInfo, String dataFromCallBackQuery, DefaultAbsSender sender) throws TelegramApiException {
        String typeOfCallBackQuery = dataFromCallBackQuery.split(" ")[1];
        Long numberOfOrder = Long.parseLong(dataFromCallBackQuery.split(" ")[2]);
        String city = dataFromCallBackQuery.split(" ")[3];
        userInfo.setNumberOfOrder(numberOfOrder);
        userInfo.setCity(city);

        switch (typeOfCallBackQuery) {
            case RIGHT_AD, LEFT_AD -> getNextOrderQuery(userInfo, sender);
            case SEE_PHOTOS_AD -> getOrderPhotosQuery(userInfo, sender);
        }
    }

    void getOrderPhotosQuery(UserInfo userInfo, DefaultAbsSender sender) throws TelegramApiException {
        List<Order> orders = getUserOrders(userInfo.getCity());
        int indexOfCurrentOrder = getIndexOfOrder(orders, userInfo.getNumberOfOrder());
        Order newCurrentOrder = orders.get(indexOfCurrentOrder);

        var photos = newCurrentOrder.getPhotos();
        if (photos.size() == 1) {
            sender.execute(MessageUtil.getMessage(userInfo.getChatId(), photos.get(0).getId(), userInfo.getMessageId()));
        } else {
            List<InputMedia> listPhotos = new ArrayList<>();
            photos.forEach(photo -> listPhotos.add(new InputMediaPhoto(photo.getId())));
            sender.execute(MessageUtil.getMessage(userInfo.getChatId(), listPhotos, userInfo.getMessageId()));
        }
    }

    private void getNextOrderQuery(UserInfo userInfo, DefaultAbsSender sender) throws TelegramApiException {
        List<Order> orders = getUserOrders(userInfo.getCity());

        int indexOfCurrentOrder = getIndexOfOrder(orders, userInfo.getNumberOfOrder());
        Order newCurrentOrder = orders.get(indexOfCurrentOrder);
        String description = newCurrentOrder.getDescription();
        String newMainPhotoId = newCurrentOrder.getPhotos().get(0).getId();

        int[] indexes = getIndexesOfNeighboringOrders(indexOfCurrentOrder, orders.size());
        Order leftNewOrder = orders.get(indexes[0]);
        Order rightNewOrder = orders.get(indexes[1]);

        InlineKeyboardMarkup keyboard = KeyBoardUtil.seeOtherADsKeyboard(leftNewOrder.getId(),
                newCurrentOrder.getId(), rightNewOrder.getId(), userInfo.getCity());
        String price = "\n<b>Цена: </b>" + newCurrentOrder.getPrice();
        sender.execute(MessageUtil.getEditMessageForSeeAds(userInfo.getChatId(), userInfo.getMessageId(),
                newMainPhotoId, description + price, keyboard));
    }

    private List<Order> getUserOrders(String city) {
        return orderService.findOrdersByCity(city)
                .stream()
                .sorted(Comparator.comparing(Order::getId))
                .toList();
    }

    private int getIndexOfOrder(List<Order> orders, Long numberOfOrder) {
        for (int i = 0; i < orders.size(); ++i) {
            Order order = orders.get(i);
            if (order.getId().equals(numberOfOrder)) {
                return i;
            }
        }
        return 0;
    }

    private int[] getIndexesOfNeighboringOrders(int indexOfCurrentOrder, int size) {
        int[] indexes = new int[2];

        if (indexOfCurrentOrder == 0) {
            indexes[0] = size - 1;
            indexes[1] = indexOfCurrentOrder + 1;
        } else if (indexOfCurrentOrder == size - 1) {
            indexes[0] = indexOfCurrentOrder - 1;
            indexes[1] = 0;
        } else {
            indexes[0] = indexOfCurrentOrder - 1;
            indexes[1] = indexOfCurrentOrder + 1;
        }

        return indexes;
    }
}
