package com.kolyapetrov.telegram_bot.model.service;

import com.kolyapetrov.telegram_bot.model.entity.AppUser;
import com.kolyapetrov.telegram_bot.model.entity.UserState;
import com.kolyapetrov.telegram_bot.model.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.User;

import static com.kolyapetrov.telegram_bot.model.entity.UserState.REGISTRATION;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void saveUser(AppUser appUser) {
        userRepository.save(appUser);
    }

    @Override
    public AppUser getUser(User telegramUser) {
        AppUser appUser = userRepository
                .findById(telegramUser.getId())
                .orElse(null);

        if (appUser == null) {
            AppUser transientUser = AppUser.builder()
                    .userId(telegramUser.getId())
                    .nameOfUser(telegramUser.getUserName())
                    .userState(REGISTRATION)
                    .build();
            return userRepository.save(transientUser);
        }
        return appUser;
    }
}
