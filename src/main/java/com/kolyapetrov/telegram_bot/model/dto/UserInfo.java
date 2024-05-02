package com.kolyapetrov.telegram_bot.model.dto;

import com.kolyapetrov.telegram_bot.model.entity.AppUser;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserInfo {
    private AppUser appUser;
    private String chatId;
    private Integer messageId;
}
