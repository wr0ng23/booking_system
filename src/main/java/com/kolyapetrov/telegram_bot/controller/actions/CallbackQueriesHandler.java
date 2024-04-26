package com.kolyapetrov.telegram_bot.controller.actions;

import com.kolyapetrov.telegram_bot.model.dto.UserInfo;
import com.kolyapetrov.telegram_bot.model.entity.AppUser;
import com.kolyapetrov.telegram_bot.model.entity.Order;
import com.kolyapetrov.telegram_bot.model.service.OrderService;
import com.kolyapetrov.telegram_bot.model.service.UserService;
import com.kolyapetrov.telegram_bot.util.KeyBoardUtil;
import com.kolyapetrov.telegram_bot.util.MessageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.media.InputMedia;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static com.kolyapetrov.telegram_bot.util.ConstantMessages.*;

@Component
public class CallbackQueriesHandler {
    private final UserService userService;
    private final OrderService orderService;

    @Autowired
    public CallbackQueriesHandler(UserService userService, OrderService orderService) {
        this.userService = userService;
        this.orderService = orderService;
    }

    public void handle(Update update, DefaultAbsSender sender) throws TelegramApiException {
        String dataFromCallBackQuery = update.getCallbackQuery().getData();
        AppUser appUser = userService.getUser(update.getCallbackQuery().getFrom());
        String chatId = update.getCallbackQuery().getFrom().getId().toString();
        Integer messageId = update.getCallbackQuery().getMessage().getMessageId();
        Long numberOfOrder = Long.parseLong(dataFromCallBackQuery.split(" ")[1]);
        UserInfo userInfo = new UserInfo(appUser, chatId, messageId, numberOfOrder);

        if (dataFromCallBackQuery.startsWith(RIGHT_AD) || dataFromCallBackQuery.startsWith(LEFT_AD)) {
            getNextOrderQuery(userInfo, sender);
        } else if (dataFromCallBackQuery.startsWith(SEE_PHOTOS_AD)) {
            getOrderPhotosQuery(userInfo, sender);
        } else if (dataFromCallBackQuery.startsWith(DELETE_AD)) {
            deleteOrderQuery(userInfo, sender);
        }
    }

    private void deleteOrderQuery(UserInfo userInfo, DefaultAbsSender sender) throws TelegramApiException {
        List<Order> orders = userInfo.getAppUser().getOrders();
        int index = getIndexOfOrder(orders, userInfo.getNumberOfOrder());
        orderService.deleteOrder(orders.get(index));

        int[] indexes = getIndexesOfNeighboringOrders(index, orders.size());
        userInfo.setNumberOfOrder(orders.get(indexes[1]).getNumberOfOrder());
        getNextOrderQuery(userInfo, sender);
    }

    private void getOrderPhotosQuery(UserInfo userInfo, DefaultAbsSender sender) throws TelegramApiException {
        List<Order> userOrders = getUserOrders(userInfo.getAppUser());
        int indexOfCurrentOrder = getIndexOfOrder(userOrders, userInfo.getNumberOfOrder());
        Order newCurrentOrder = userOrders.get(indexOfCurrentOrder);

        var photos = newCurrentOrder.getPhotos();
        List<InputMedia> listPhotos = new ArrayList<>();
        photos.forEach(photo -> listPhotos.add(new InputMediaPhoto(photo.getId())));
        sender.execute(MessageUtil.getMessage(userInfo.getChatId(), listPhotos, userInfo.getMessageId()));

        /*DeleteMessage deleteMessage = new DeleteMessage(chatId, messageId);
        sender.execute(deleteMessage);*/

        /*String description = newCurrentOrder.getDescription();
        String mainPhotoId = newCurrentOrder.getPhotos().get(0).getId();
        int[] indexes = getIndexesOfNeighboringOrders(indexOfCurrentOrder, userOrders.size());
        sender.execute(MessageUtil.getMessage(chatId, description, mainPhotoId,
                KeyBoardUtil.seeADsKeyboard(String.valueOf(indexes[0]), newCurrentOrder.getNumberOfOrder().toString(),
                        String.valueOf(indexes[1]))));
*/    }

    private void getNextOrderQuery(UserInfo userInfo, DefaultAbsSender sender) throws TelegramApiException {
        List<Order> userOrders = getUserOrders(userInfo.getAppUser());

        int indexOfCurrentOrder = getIndexOfOrder(userOrders, userInfo.getNumberOfOrder());
        Order newCurrentOrder = userOrders.get(indexOfCurrentOrder);
        String description = newCurrentOrder.getDescription();
        String newMainPhotoId = newCurrentOrder.getPhotos().get(0).getId();

        int[] indexes = getIndexesOfNeighboringOrders(indexOfCurrentOrder, userOrders.size());
        Order leftNewOrder = userOrders.get(indexes[0]);
        Order rightNewOrder = userOrders.get(indexes[1]);

        InlineKeyboardMarkup keyboard = KeyBoardUtil.seeADsKeyboard(leftNewOrder.getNumberOfOrder().toString(),
                newCurrentOrder.getNumberOfOrder().toString(), rightNewOrder.getNumberOfOrder().toString());

        sender.execute(MessageUtil.getEditMessageForSeeAds(userInfo.getChatId(), userInfo.getMessageId(),
                newMainPhotoId, description, keyboard));

    }

    private List<Order> getUserOrders(AppUser appUser) {
        return appUser.getOrders()
                .stream()
                .sorted(Comparator.comparing(Order::getNumberOfOrder))
                .toList();
    }

    private int getIndexOfOrder(List<Order> orders, Long numberOfOrder) {
        for (int i = 0; i < orders.size(); ++i) {
            Order order = orders.get(i);
            if (order.getNumberOfOrder().equals(numberOfOrder)) {
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
