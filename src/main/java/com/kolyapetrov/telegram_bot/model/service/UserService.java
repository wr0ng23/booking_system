package com.kolyapetrov.telegram_bot.model.service;

import com.kolyapetrov.telegram_bot.model.entity.AppUser;
import org.telegram.telegrambots.meta.api.objects.User;

public interface UserService {
    void saveUser(AppUser appUser);
    AppUser getUser(User telegramUser);
}
