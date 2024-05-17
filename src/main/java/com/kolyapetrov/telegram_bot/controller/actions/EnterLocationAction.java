package com.kolyapetrov.telegram_bot.controller.actions;

import com.kolyapetrov.telegram_bot.controller.ActionHandler;
import com.kolyapetrov.telegram_bot.model.entity.AppUser;
import com.kolyapetrov.telegram_bot.model.entity.Order;
import com.kolyapetrov.telegram_bot.model.entity.PhotoOfOrder;
import com.kolyapetrov.telegram_bot.model.service.LocationService;
import com.kolyapetrov.telegram_bot.model.service.OrderService;
import com.kolyapetrov.telegram_bot.model.service.SearchAdsFilterService;
import com.kolyapetrov.telegram_bot.model.service.UserService;
import com.kolyapetrov.telegram_bot.util.KeyBoardUtil;
import com.kolyapetrov.telegram_bot.util.MessageUtil;
import com.kolyapetrov.telegram_bot.util.OrderUtil;
import com.kolyapetrov.telegram_bot.util.enums.UserState;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.meta.api.objects.Location;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Comparator;
import java.util.List;

import static com.kolyapetrov.telegram_bot.util.ConstantMessages.GO_BACK;
import static com.kolyapetrov.telegram_bot.util.enums.UserState.MAIN;

@Component
public class EnterLocationAction implements ActionHandler {
    private final UserService userService;
    private final LocationService locationService;
    private final OrderService orderService;
    private final SearchAdsFilterService searchAdsFilterService;

    public EnterLocationAction(UserService userService, LocationService locationService, OrderService orderService,
                               SearchAdsFilterService searchAdsFilterService) {
        this.userService = userService;
        this.locationService = locationService;
        this.orderService = orderService;
        this.searchAdsFilterService = searchAdsFilterService;
    }

    @Override
    public UserState getState() {
        return UserState.ENTER_LOCATION_CORDS;
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
            Long recordId = searchAdsFilterService.findRecordIdByUserId(appUser.getUserId());
            searchAdsFilterService.updateLatitude(recordId, location.getLatitude());
            searchAdsFilterService.updateLongitude(recordId, location.getLongitude());

            System.out.println("My lat: " + location.getLatitude());
            System.out.println("My lon: " + location.getLongitude());
            appUser.setUserState(UserState.MAIN);
            userService.saveUser(appUser);
            sender.execute(MessageUtil.getMessage(chatId, "Координаты текущего местоположения получены!",
                    KeyBoardUtil.mainKeyBoard()));
            /*String city = locationService.requestInfoAboutLocationByCords(location.getLatitude(), location.getLongitude());
            List<Order> orders = orderService.findByCityAndUserIdNot(city, appUser.getUserId());

            orders.sort(Comparator.comparingDouble(myOrder ->
                    locationService.distBetweenPoints(myOrder.getLatitude(), myOrder.getLongitude(), location.getLatitude(),
                            location.getLongitude())));*/

            /*if (orders.isEmpty()) {
                sender.execute(MessageUtil.getMessage(chatId, "Вокруг вас не найдено объявлений. Вы можете " +
                        "воспользоваться поиском по городу!"));

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
            }*/

        } else {
            sender.execute(MessageUtil.getMessage(chatId, "Для отправки геопозиции нажмите кнопку снизу!"));
        }
    }
}
