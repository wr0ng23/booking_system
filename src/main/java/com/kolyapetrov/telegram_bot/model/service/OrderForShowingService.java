package com.kolyapetrov.telegram_bot.model.service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class OrderForShowingService {
    private final JdbcTemplate jdbcTemplate;

    public OrderForShowingService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void createTempTable() {
        String createTableQuery = "CREATE TEMP TABLE IF NOT EXISTS orders_for_showing" +
                "(id SERIAL PRIMARY KEY, id_of_order int, user_id int, message_id int, last_update_time timestamp)";
        jdbcTemplate.execute(createTableQuery);
    }
}
