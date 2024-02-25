package com.kolyapetrov.telegram_bot.controller.commands;

import com.kolyapetrov.telegram_bot.controller.CommandHandler;
import com.kolyapetrov.telegram_bot.model.entity.AppUser;
import com.kolyapetrov.telegram_bot.model.entity.UserState;
import com.kolyapetrov.telegram_bot.model.service.UserService;
import com.kolyapetrov.telegram_bot.util.Command;
import com.kolyapetrov.telegram_bot.util.KeyBoardUtil;
import com.kolyapetrov.telegram_bot.util.MessageUtil;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class CreateNewAdCommand implements CommandHandler {
    private final UserService userService;

    public CreateNewAdCommand(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void handle(Update update, DefaultAbsSender sender) throws TelegramApiException {
        AppUser appUser = userService.getUser(update.getMessage().getFrom());
        String chatId = update.getMessage().getChatId().toString();

        sender.execute(MessageUtil.getMessage(chatId, "Для создания объявления отправьте фотографии " +
                "вашего жилья. Когда отправите нужное количество - нажмите кнопку снизу.", KeyBoardUtil.finishPhotoSending()));
        appUser.setUserState(UserState.ENTER_PHOTOS);
        userService.saveUser(appUser);
    }

    @Override
    public Command getCommand() {
        return Command.CREATE_NEW_ADVERTISEMENT;
    }
}
