package com.kolyapetrov.telegram_bot.controller.callbacks;

import com.kolyapetrov.telegram_bot.controller.CallBackHandler;
import com.kolyapetrov.telegram_bot.model.dto.CallBackInfo;
import com.kolyapetrov.telegram_bot.model.dto.UserInfo;
import com.kolyapetrov.telegram_bot.model.entity.AppUser;
import com.kolyapetrov.telegram_bot.model.entity.Filter;
import com.kolyapetrov.telegram_bot.model.entity.Order;
import com.kolyapetrov.telegram_bot.model.entity.PhotoOfOrder;
import com.kolyapetrov.telegram_bot.model.service.*;
import com.kolyapetrov.telegram_bot.util.KeyBoardUtil;
import com.kolyapetrov.telegram_bot.util.MessageUtil;
import com.kolyapetrov.telegram_bot.util.OrderUtil;
import com.kolyapetrov.telegram_bot.util.enums.CallBackName;
import com.kolyapetrov.telegram_bot.util.enums.UserState;
import org.aspectj.weaver.ast.Or;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static com.kolyapetrov.telegram_bot.util.ConstantMessages.*;

@Component
public class SearchFilterCallBackQuery implements CallBackHandler {
    private final OrderService orderService;
    private final UserService userService;
    private final SearchAdsFilterService searchAdsFilterService;
    private final OrdersInMessageService ordersInMessageService;
    private final LocationService locationService;

    public SearchFilterCallBackQuery(OrderService orderService, UserService userService,
                                     SearchAdsFilterService searchAdsFilterService,
                                     OrdersInMessageService ordersInMessageService, LocationService locationService) {
        this.orderService = orderService;
        this.userService = userService;
        this.searchAdsFilterService = searchAdsFilterService;
        this.ordersInMessageService = ordersInMessageService;
        this.locationService = locationService;
    }

    @Override
    public CallBackName getCallBack() {
        return CallBackName.SEARCH_FILTER;
    }

    @Override
    public void handle(UserInfo userInfo, CallBackInfo callBackInfo, DefaultAbsSender sender) throws TelegramApiException {
        String nameOfButton = callBackInfo.getNameOfButton();

        if (!searchAdsFilterService.isDatabaseCreated()) {
            sender.execute(MessageUtil.getAnswerCallbackQuery(callBackInfo.getId(), "Срок действия фильтра истек"));
            DeleteMessage deleteMessage = new DeleteMessage(userInfo.getChatId(), userInfo.getMessageId());
            sender.execute(deleteMessage);
            return;
        }

        Long userId = userInfo.getAppUser().getUserId();
        searchAdsFilterService.updateLastUpdateTime(userId);

        if (nameOfButton.startsWith(ENTER_CITY)) {
            action(userInfo, callBackInfo, sender, "<i>Название города: </i>",
                    "Введите название города", UserState.ENTER_CITY_FOR_FILTER, KeyBoardUtil.cancelFilter());

        } else if (nameOfButton.startsWith(ENTER_LOWER_PRICE)) {
            action(userInfo, callBackInfo, sender, "<i>Нижняя граница цены: </i>",
                    "Введите нижнюю границу цену", UserState.ENTER_LOWER_PRICE_FOR_FILTER,
                    KeyBoardUtil.cancelFilter());

        } else if (nameOfButton.startsWith(ENTER_UPPER_PRICE)) {
            action(userInfo, callBackInfo, sender, "<i>Верхняя граница цены: </i>",
                    "Введите верхнюю границу цену", UserState.ENTER_UPPER_PRICE_FOR_FILTER,
                    KeyBoardUtil.cancelFilter());

        } else if (nameOfButton.startsWith(ENTER_LOWER_DATE)) {
            Long recordId = searchAdsFilterService.findRecordIdByUserId(userInfo.getAppUser().getUserId());
            LocalDate upperDate = searchAdsFilterService.findUpperDateById(recordId);
            LocalDate lowerDate = searchAdsFilterService.findLowerDateByUserId(recordId);

            String text = "Ввод даты начала бронирования:\n\uD83D\uDCC5 - текущая дата начала бронирования\n\uD83C\uDFC1" +
                    " - текущая дата конца бронирования";
            action(userInfo, callBackInfo, sender, text, "Введите дату начала бронирования",
                    UserState.ENTER_LOWER_DATE_FOR_FILTER, KeyBoardUtil.getKeyBoardCalendarForSearching(lowerDate, upperDate));

        } else if (nameOfButton.startsWith(ENTER_UPPER_DATE)) {
            Long recordId = searchAdsFilterService.findRecordIdByUserId(userInfo.getAppUser().getUserId());
            LocalDate lowerDate = searchAdsFilterService.findLowerDateByUserId(recordId);
            LocalDate upperDate = searchAdsFilterService.findUpperDateById(recordId);

            String text = "Ввод даты конца бронирования:\n\uD83D\uDCC5 - текущая дата начала бронирования\n\uD83C\uDFC1 " +
                    "- текущая дата конца бронирования";
            action(userInfo, callBackInfo, sender, text, "Введите дату конца бронирования",
                    UserState.ENTER_UPPER_DATE_FOR_FILTER, KeyBoardUtil.getKeyBoardCalendarForSearching(lowerDate, upperDate));

        } else if (nameOfButton.startsWith(SEARCH)) {
            search(userInfo, callBackInfo, sender);

        } else if (nameOfButton.startsWith(SEND_LOCATION)) {
            action(userInfo, callBackInfo, sender, "Получение геопозиции",
                    "Нажмите кнопку снизу для отправки геопозиции", UserState.ENTER_LOCATION_CORDS,
                    KeyBoardUtil.choiceLocationOrEnterCity());
        }
    }

    private void action(UserInfo userInfo, CallBackInfo callBackInfo, DefaultAbsSender sender, String message,
                        String callbackAlert, UserState state, ReplyKeyboard keyboardMarkup)
            throws TelegramApiException {
        sender.execute(MessageUtil.getMessage(userInfo.getChatId(), message, keyboardMarkup));
        sender.execute(MessageUtil.getAnswerCallbackQuery(callBackInfo.getId(), callbackAlert));
        AppUser user = userInfo.getAppUser();
        user.setUserState(state);
        userService.saveUser(user);
    }

    private void search(UserInfo userInfo, CallBackInfo callBackInfo, DefaultAbsSender sender)
            throws TelegramApiException {
        Long recordId = searchAdsFilterService.findRecordIdByUserId(userInfo.getAppUser().getUserId());
        Filter filter = searchAdsFilterService.getRecordById(recordId);
        System.out.println("FILTER CORDS: " + filter.getLatitude() + " " + filter.getLongitude());

        if (filter != null && filter.getCity() == null && filter.getLatitude() == null && filter.getLongitude() == null) {
            sender.execute(MessageUtil.getAnswerCallbackQuery(callBackInfo.getId(), "Необходимо выбрать населенный пункт " +
                    "для поиска или воспользоваться отправкой геопозиции!"));
            return;
        }

        if (filter != null && filter.getLowerDate() == null && filter.getUpperDate() == null) {
            sender.execute(MessageUtil.getAnswerCallbackQuery(callBackInfo.getId(), "Необходимо выбрать даты для поиска! "));
            return;
        }

        List<Order> orders;
        List<Double> distances = new ArrayList<>();
        if (filter.getLongitude() != null && filter.getLatitude() != null) {
            String city = locationService.requestInfoAboutLocationByCords(filter.getLatitude(), filter.getLongitude());
            filter.setCity(city);
            orders = orderService.findOrdersByFilter(filter);
            orders.sort(Comparator.comparingDouble(myOrder ->  {
                double distance = locationService.distBetweenPoints(myOrder.getLatitude(), myOrder.getLongitude(), filter.getLatitude(), filter.getLongitude());
                distances.add(distance);
                return distance;
            }));
            distances.sort(Comparator.comparingDouble(Double::doubleValue));

        } else {
            orders = orderService.findOrdersByFilter(filter)
                    .stream()
                    .sorted(Comparator.comparingLong(Order::getId))
                    .toList();
        }

        if (orders.isEmpty()) {
            sender.execute(MessageUtil.getAnswerCallbackQuery(callBackInfo.getId(), "К сожалению объявлений с " +
                    "указанными параметрами не найдено!"));
            return;
        }

        var sentMessage = sender.execute(MessageUtil.getMessage(userInfo.getChatId(),
                "Найденные объявления:"));

        List<Long> idsOfOrders = orders.stream().map(order -> order.getId()).toList();
        var firstOrder = orders.get(0);
        List<PhotoOfOrder> photosOfOrder = firstOrder.getPhotos();
        String mainPhotoId = photosOfOrder.get(0).getId();

        if (filter.getLongitude() != null && filter.getLatitude() != null) {
            String distanceToPerson = OrderUtil.getDistanceToPerson(distances.get(0));
            if (orders.size() > 1) {
                sender.execute(MessageUtil.getMessage(userInfo.getChatId(), firstOrder + distanceToPerson, mainPhotoId,
                        KeyBoardUtil.seeLocalAdsKeyboard(firstOrder.getId(), (long) (sentMessage.getMessageId() + 1))));
            } else {
                sender.execute(MessageUtil.getMessage(userInfo.getChatId(), firstOrder + distanceToPerson, mainPhotoId,
                        KeyBoardUtil.seeLocalAdsKeyboard2(firstOrder.getId(), (long) (sentMessage.getMessageId() + 1))));
            }
            ordersInMessageService.saveAll(idsOfOrders, (long) (sentMessage.getMessageId() + 1), filter.getUserId(), distances);

        } else {
            if (orders.size() > 1) {
                sender.execute(MessageUtil.getMessage(userInfo.getChatId(), firstOrder.toString(), mainPhotoId,
                        KeyBoardUtil.seeOtherADsKeyboard(firstOrder.getId(), (long) (sentMessage.getMessageId() + 1))));
            } else {
                sender.execute(MessageUtil.getMessage(userInfo.getChatId(), firstOrder.toString(), mainPhotoId,
                        KeyBoardUtil.seeOtherADsKeyboard2(firstOrder.getId(), (long) (sentMessage.getMessageId() + 1))));
            }
            ordersInMessageService.saveAll(idsOfOrders, (long) (sentMessage.getMessageId() + 1), filter.getUserId());
        }
    }
}
