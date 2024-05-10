package com.kolyapetrov.telegram_bot.controller.actions;

import com.kolyapetrov.telegram_bot.controller.ActionHandler;
import com.kolyapetrov.telegram_bot.model.entity.AppUser;
import com.kolyapetrov.telegram_bot.model.entity.Order;
import com.kolyapetrov.telegram_bot.model.entity.PhotoOfOrder;
import com.kolyapetrov.telegram_bot.model.service.LocationService;
import com.kolyapetrov.telegram_bot.model.service.OrderService;
import com.kolyapetrov.telegram_bot.model.service.UserService;
import com.kolyapetrov.telegram_bot.util.KeyBoardUtil;
import com.kolyapetrov.telegram_bot.util.MessageUtil;
import com.kolyapetrov.telegram_bot.util.OrderUtil;
import com.kolyapetrov.telegram_bot.util.enums.UserState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.meta.api.objects.Location;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Comparator;
import java.util.List;

import static com.kolyapetrov.telegram_bot.util.ConstantMessages.GO_BACK;
import static com.kolyapetrov.telegram_bot.util.enums.UserState.MAIN;
import static com.kolyapetrov.telegram_bot.util.enums.UserState.SEARCH_FOR_ADS;

@Component
public class EnterLocationForSeeingAdsAction implements ActionHandler {
    private final UserService userService;
    private final LocationService locationService;
    private final OrderService orderService;

    @Autowired
    public EnterLocationForSeeingAdsAction(UserService userService, OrderService orderService,
                                           LocationService locationService) {
        this.userService = userService;
        this.orderService = orderService;
        this.locationService = locationService;
    }

    @Override
    public UserState getState() {
        return SEARCH_FOR_ADS;
    }

    @Override
    public void handle(Update update, DefaultAbsSender sender) throws TelegramApiException {
        String chatId = update.getMessage().getChatId().toString();
        AppUser appUser = userService.getUser(update.getMessage().getFrom());

        if (update.getMessage().hasText() && update.getMessage().getText().startsWith(GO_BACK)) {
            appUser.setUserState(MAIN);
            userService.saveUser(appUser);
            sender.execute(MessageUtil.getMessage(chatId, GO_BACK, KeyBoardUtil.mainKeyBoard()));

        } else if (update.getMessage().hasLocation()) {
            Location location = update.getMessage().getLocation();
            System.out.println("My lat: " + location.getLatitude());
            System.out.println("My lon: " + location.getLongitude());
            String city = locationService.requestInfoAboutLocationByCords(location.getLatitude(), location.getLongitude());
            List<Order> orders = orderService.findByCityAndUserIdNot(city, appUser.getUserId());

            orders.sort(Comparator.comparingDouble(myOrder ->
                    locationService.distBetweenPoints(myOrder.getLatitude(), myOrder.getLongitude(), location.getLatitude(),
                            location.getLongitude())));

            if (orders.isEmpty()) {
                sender.execute(MessageUtil.getMessage(chatId, "Вокруг вас не найдено объявлений!"));

            } else {
                var order = orders.get(0);
                List<PhotoOfOrder> photosOfOrder = order.getPhotos();
                String mainPhotoId = photosOfOrder.get(0).getId();
                double distanceInMeters = locationService.distBetweenPoints(order.getLatitude(), order.getLongitude(),
                        location.getLatitude(), location.getLongitude());
                String distanceToPerson = OrderUtil.getDistanceToPerson(distanceInMeters);

                if (orders.size() > 1) {
                    Long leftOrderId = orders.get(orders.size() - 1).getId();
                    Long currentOrderId = orders.get(0).getId();
                    Long rightOrderId = orders.get(1).getId();

                    sender.execute(MessageUtil.getMessage(chatId, order + distanceToPerson, mainPhotoId,
                            KeyBoardUtil.seeLocalAdsKeyboard(leftOrderId, currentOrderId, rightOrderId, location.getLatitude(),
                                    location.getLongitude())));
                } else {
                    sender.execute(MessageUtil.getMessage(chatId, order + distanceToPerson, mainPhotoId,
                            KeyBoardUtil.seeLocalAdsKeyboard(order.getId())));
                }
            }

        } else if (update.getMessage().hasText()) {
            String city = update.getMessage().getText();
            String capitalizedCity = (Character.toUpperCase(city.charAt(0)) + city.substring(1));
            List<Order> orders = orderService.findByCityAndUserIdNot(capitalizedCity, appUser.getUserId());

            if (orders.isEmpty()) {
                sender.execute(MessageUtil.getMessage(chatId, "В выбранном городе объявлений не найдено!"));
            } else {
                var firstOrder = orders.get(0);
                List<PhotoOfOrder> photosOfOrder = firstOrder.getPhotos();
                String mainPhotoId = photosOfOrder.get(0).getId();

                if (orders.size() > 1) {
                    Long leftOrderId = orders.get(orders.size() - 1).getId();
                    Long currentOrderId = orders.get(0).getId();
                    Long rightOrderId = orders.get(1).getId();
                    sender.execute(MessageUtil.getMessage(chatId, firstOrder.toString(), mainPhotoId,
                            KeyBoardUtil.seeOtherADsKeyboard(leftOrderId, currentOrderId, rightOrderId, capitalizedCity)));
                } else {
                    sender.execute(MessageUtil.getMessage(chatId, firstOrder.toString(), mainPhotoId,
                            KeyBoardUtil.seeOtherADsKeyboard(orders.get(0).getId(), capitalizedCity)));
                }
            }
        } else {
            sender.execute(MessageUtil.getMessage(chatId, "Введите название города текстом или " +
                    "отправьте геопозицию!"));
        }
    }
}
