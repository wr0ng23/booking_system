package com.kolyapetrov.telegram_bot.controller.actions;

import com.kolyapetrov.telegram_bot.controller.ActionHandler;
import com.kolyapetrov.telegram_bot.model.entity.AppUser;
import com.kolyapetrov.telegram_bot.model.entity.Order;
import com.kolyapetrov.telegram_bot.model.entity.PhotoOfOrder;
import com.kolyapetrov.telegram_bot.util.UserState;
import com.kolyapetrov.telegram_bot.model.service.UserService;
import com.kolyapetrov.telegram_bot.util.ConstantMessages;
import com.kolyapetrov.telegram_bot.util.KeyBoardUtil;
import com.kolyapetrov.telegram_bot.util.MessageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.Comparator;

@Component
public class EnterPhotosAction implements ActionHandler {
    private final UserService userService;

    @Autowired
    public EnterPhotosAction(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void handle(Update update, DefaultAbsSender sender) throws TelegramApiException {
        String chatId = update.getMessage().getChatId().toString();
        AppUser appUser = userService.getUser(update.getMessage().getFrom());

        if (update.getMessage().hasPhoto()) {
            int size = update.getMessage().getPhoto().size();
            String fileId = update.getMessage().getPhoto().get(size - 1).getFileId();
            System.out.println("File id of photo: " + fileId);

            Order lastOrder = getLastOrder(appUser);
            // creating new record about photo id and checking count of photos for new order
            if (lastOrder.getPhotos().size() < 10) {
                lastOrder.getPhotos().add(new PhotoOfOrder(fileId));
                appUser.getOrders().add(lastOrder);
                userService.saveUser(appUser);
            } else {
                sender.execute(MessageUtil.getMessage(chatId, "Вы не можете создать объявление в котором " +
                        "больше 10 фотографий! Нажмите кнопку cнизу чтобы подтвердить отправку фотографий."));
            }

        } else if (update.getMessage().hasText() &&
                update.getMessage().getText().startsWith(ConstantMessages.FINISH_SENDING_PHOTOS)) {

            Order lastOrder = getLastOrder(appUser);
            if (lastOrder.getPhotos().isEmpty()) {
                sender.execute(MessageUtil.getMessage(chatId, "Вам необходимо отправить минмум 1 фото!"));
                return;
            }
            appUser.setUserState(UserState.ENTER_DESCRIPTION_OF_AD);
            userService.saveUser(appUser);
            sender.execute(MessageUtil.getMessage(chatId, "Теперь отправьте описание, " +
                    "которое будут видеть люди просматривающие ваше объявление"));
        } else {
            sender.execute(MessageUtil.getMessage(chatId, "Отправьте фото вашего жилья и " +
                    "нажмите кнопку снизу, чтобы перейти дальше", KeyBoardUtil.finishPhotoSending()));
        }
    }

    private Order getLastOrder(AppUser appUser) {
        long sizeOfOrders = appUser.getOrders().size();
        Order lastOrder;

        // Getting last order if it exists or creating a new order
        if (sizeOfOrders != 0) {
            var orders = appUser.getOrders()
                    .stream()
                    .sorted(Comparator.comparing(Order::getId))
                    .toList();
            lastOrder = orders.get((int) (sizeOfOrders - 1));
        } else {
            lastOrder = new Order();
            lastOrder.setPhotos(new ArrayList<>());
            lastOrder.setIsEditing(false);
        }

        // checking if order was already created
        if (lastOrder.getDescription() != null) {
            lastOrder = new Order();
            lastOrder.setPhotos(new ArrayList<>());
            lastOrder.setIsEditing(false);
        }
        return lastOrder;
    }

    @Override
    public UserState getState() {
        return UserState.ENTER_PHOTOS;
    }
}
