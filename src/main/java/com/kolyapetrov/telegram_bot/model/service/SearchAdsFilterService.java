package com.kolyapetrov.telegram_bot.model.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class SearchAdsFilterService {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public SearchAdsFilterService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void createTempTable() {
        String createTableQuery = "CREATE TEMP TABLE IF NOT EXISTS filter_for_ads" +
                "(id SERIAL PRIMARY KEY, city varchar, start_price int, end_price int, start_date DATE, end_date DATE, " +
                "user_id int, last_update_time timestamp)";
        jdbcTemplate.execute(createTableQuery);
    }

    public void dropTempTable() {
        String dropTableQuery = "DROP TABLE IF EXISTS filter_for_ads";
        jdbcTemplate.execute(dropTableQuery);
    }

    public Long findRecordByUserId(Long user_id) {
        String selectQuery = "SELECT id FROM filter_for_ads WHERE user_id = ? ORDER BY last_update_time DESC";
        List<Long> result = jdbcTemplate.queryForList(selectQuery, Long.class, user_id);
        return result.isEmpty() ? null : result.get(0);
    }

    public void updateStartDate(Long idOfRecord, String start_date) {
        LocalDate date = LocalDate.parse(start_date);
        String updateQuery = "UPDATE filter_for_ads SET start_date = ?, last_update_time = ? WHERE id = ?";
        jdbcTemplate.update(updateQuery, date, LocalDateTime.now(), idOfRecord);
    }

    public void updateCity(Long idOfRecord, String city) {
        String updateQuery = "UPDATE filter_for_ads SET city = ?, last_update_time = ? WHERE id = ?";
        jdbcTemplate.update(updateQuery, city, LocalDateTime.now(), idOfRecord);
    }

    public void updateLastUpdateTime(Long userId) {
        Long recordId = findRecordByUserId(userId);
        String updateQuery = "UPDATE filter_for_ads SET last_update_time = ? WHERE id = ?";
        jdbcTemplate.update(updateQuery, LocalDateTime.now(), recordId);
    }

    public long insertNewRecord(Long userId) {
        String insertQuery = "INSERT INTO filter_for_ads (user_id, last_update_time) VALUES (?, ?)";
        jdbcTemplate.update(insertQuery, userId, LocalDateTime.now());

        return findRecordByUserId(userId);
    }

    public boolean isDatabaseCreated() {
        String selectQuery = "SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME='filter_for_ads'";
        return !jdbcTemplate.queryForList(selectQuery).isEmpty();
    }
}
