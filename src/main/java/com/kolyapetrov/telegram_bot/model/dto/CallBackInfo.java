package com.kolyapetrov.telegram_bot.model.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CallBackInfo {
    private Long numberOfOrder;
    private String city;
    private Double longitude;
    private Double latitude;
    private String nameOfButton;
}
