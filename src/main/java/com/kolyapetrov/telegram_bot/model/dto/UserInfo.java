package com.kolyapetrov.telegram_bot.model.dto;

import com.kolyapetrov.telegram_bot.model.entity.AppUser;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserInfo {
    private AppUser appUser;
    private String chatId;
    private Integer messageId;
    private Long numberOfOrder;

    public UserInfo(AppUser appUser, String chatId, Integer messageId, Long numberOfOrder) {
        this.appUser = appUser;
        this.chatId = chatId;
        this.messageId = messageId;
        this.numberOfOrder = numberOfOrder;
    }
}
