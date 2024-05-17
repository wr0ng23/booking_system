package com.kolyapetrov.telegram_bot.model.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CallBackInfo {
    private String id;
    private Long numberOfOrder;
    private Long messageId;
    private Double longitude;
    private Double latitude;
    private String nameOfButton;
    private String selectedDate;
}
