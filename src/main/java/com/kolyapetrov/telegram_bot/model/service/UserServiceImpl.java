package com.kolyapetrov.telegram_bot.model.service;

import com.kolyapetrov.telegram_bot.model.entity.AppUser;
import com.kolyapetrov.telegram_bot.model.entity.Order;
import com.kolyapetrov.telegram_bot.util.enums.UserState;
import com.kolyapetrov.telegram_bot.model.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.List;

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
                    .userState(UserState.MAIN)
                    .build();
            return userRepository.save(transientUser);
        }
        return appUser;
    }

    @Override
    public List<Order> getOrders(Long id) {
        AppUser appUser = userRepository.findById(id).orElse(null);
        if (appUser == null) {
            return null;
        } else {
            return appUser.getOrders();
        }
    }

    /*@Override
    public Order getOrderByNumberOfOrder(Long idOfUser, Long numberOfOrder) {
        List<Order> orders = this.getOrders(idOfUser);
        if (orders == null) return null;
        return orders.stream()
                .filter(order -> Objects.equals(order.getNumberOfOrder(), numberOfOrder))
                .findFirst()
                .orElse(null);
    }*/
}
