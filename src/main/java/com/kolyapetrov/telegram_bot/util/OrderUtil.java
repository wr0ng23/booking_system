package com.kolyapetrov.telegram_bot.util;

import com.kolyapetrov.telegram_bot.model.entity.Order;

public class OrderUtil {
    public static String getFormattedDescription(Order order) {
        return "<b>Описание:</b>\n" + order.getDescription() + "\n\n" +
                "<b>Цена:</b> " + order.getPrice() + " руб." + "\n" +
                "<b>Город:</b> " + order.getCity() + "\n" +
                "<b>Адрес:</b> " + order.getAddress();
    }
}
