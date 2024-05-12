package com.kolyapetrov.telegram_bot.util;

import com.kolyapetrov.telegram_bot.model.entity.Order;

import java.util.List;

public class OrderUtil {
    public static int getIndexOfOrder(List<Order> orders, Long numberOfOrder) {
        for (int i = 0; i < orders.size(); ++i) {
            Order order = orders.get(i);
            if (order.getId().equals(numberOfOrder)) {
                return i;
            }
        }
        return 0;
    }

    public static int[] getIndexesOfNeighboringOrders(int indexOfCurrentOrder, int size) {
        int[] indexes = new int[2];

        if (indexOfCurrentOrder == 0) {
            indexes[0] = size - 1;
            indexes[1] = indexOfCurrentOrder + 1;
        } else if (indexOfCurrentOrder == size - 1) {
            indexes[0] = indexOfCurrentOrder - 1;
            indexes[1] = 0;
        } else {
            indexes[0] = indexOfCurrentOrder - 1;
            indexes[1] = indexOfCurrentOrder + 1;
        }

        return indexes;
    }

    public static String getDistanceToPerson(double distanceInMeters) {
        String distanceToPerson;
        if (distanceInMeters > 1000.0) {
            double distanceInKilometers = distanceInMeters / 1000.0;
            String formattedDistance = String.format("%.2f", distanceInKilometers);
            distanceToPerson = "\n\nРасстояние до вас: " + formattedDistance + " км";
        } else {
            distanceToPerson = "\n\nРасстояние до вас: " + Math.round(distanceInMeters) + " м";
        }
        return distanceToPerson;
    }

    public static String getDistanceToMetro(double distanceInMeters) {
        String distanceToPerson;
        if (distanceInMeters > 1000.0) {
            double distanceInKilometers = distanceInMeters / 1000.0;
            String formattedDistance = String.format("%.2f", distanceInKilometers);
            distanceToPerson = formattedDistance + " км";
        } else {
            distanceToPerson = Math.round(distanceInMeters) + " м";
        }
        return distanceToPerson;
    }
}
