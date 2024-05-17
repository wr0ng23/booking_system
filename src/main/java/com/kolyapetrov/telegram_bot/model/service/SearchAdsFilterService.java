package com.kolyapetrov.telegram_bot.model.service;

import com.kolyapetrov.telegram_bot.model.entity.BookingTemp;
import com.kolyapetrov.telegram_bot.model.entity.Filter;
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
                "(id SERIAL PRIMARY KEY, city varchar, start_price int8, end_price int8, start_date DATE, end_date DATE, " +
                "user_id int8, longitude float8, latitude float8, distance float8, last_update_time timestamp)";
        jdbcTemplate.execute(createTableQuery);
    }

    public void dropTempTable() {
        String dropTableQuery = "DROP TABLE IF EXISTS filter_for_ads";
        jdbcTemplate.execute(dropTableQuery);
    }

    public Long findRecordIdByUserId(Long user_id) {
        String selectQuery = "SELECT id FROM filter_for_ads WHERE user_id = ? ORDER BY last_update_time DESC";
        List<Long> result = jdbcTemplate.queryForList(selectQuery, Long.class, user_id);
        return result.isEmpty() ? null : result.get(0);
    }

    public LocalDate findLowerDateByUserId(Long id) {
        String selectQuery = "SELECT start_date FROM filter_for_ads WHERE id = ? ORDER BY last_update_time DESC";
        List<LocalDate> result = jdbcTemplate.queryForList(selectQuery, LocalDate.class, id);
        return result.isEmpty() ? null : result.get(0);
    }

    public LocalDate findUpperDateById(Long id) {
        String selectQuery = "SELECT end_date FROM filter_for_ads WHERE id = ? ORDER BY last_update_time DESC";
        List<LocalDate> result = jdbcTemplate.queryForList(selectQuery, LocalDate.class, id);
        return result.isEmpty() ? null : result.get(0);
    }

    public void updateLowerDate(Long idOfRecord, String lowerDate) {
        LocalDate date = LocalDate.parse(lowerDate);
        String updateQuery = "UPDATE filter_for_ads SET start_date = ?, last_update_time = ? WHERE id = ?";
        jdbcTemplate.update(updateQuery, date, LocalDateTime.now(), idOfRecord);
    }

    public void updateDistance(Long idOfRecord, Double distance) {
        String updateQuery = "UPDATE filter_for_ads SET distance = ?, last_update_time = ? WHERE id = ?";
        jdbcTemplate.update(updateQuery, distance, LocalDateTime.now(), idOfRecord);
    }

    public void updateUpperDate(Long idOfRecord, String upperDate) {
        LocalDate date = LocalDate.parse(upperDate);
        String updateQuery = "UPDATE filter_for_ads SET end_date = ?, last_update_time = ? WHERE id = ?";
        jdbcTemplate.update(updateQuery, date, LocalDateTime.now(), idOfRecord);
    }

    public void updateLowerPrice(Long idOfRecord, Long lowerPrice) {
        String updateQuery = "UPDATE filter_for_ads SET start_price = ?, last_update_time = ? WHERE id = ?";
        jdbcTemplate.update(updateQuery, lowerPrice, LocalDateTime.now(), idOfRecord);
    }

    public void updateUpperPrice(Long idOfRecord, Long upperPrice) {
        String updateQuery = "UPDATE filter_for_ads SET end_price = ?, last_update_time = ? WHERE id = ?";
        jdbcTemplate.update(updateQuery, upperPrice, LocalDateTime.now(), idOfRecord);
    }

    public void updateCity(Long idOfRecord, String city) {
        String updateQuery = "UPDATE filter_for_ads SET city = ?, last_update_time = ? WHERE id = ?";
        jdbcTemplate.update(updateQuery, city, LocalDateTime.now(), idOfRecord);
    }

    public void updateLastUpdateTime(Long userId) {
        Long recordId = findRecordIdByUserId(userId);
        String updateQuery = "UPDATE filter_for_ads SET last_update_time = ? WHERE id = ?";
        jdbcTemplate.update(updateQuery, LocalDateTime.now(), recordId);
    }

    public void updateLatitude(Long idOfRecord, Double latitude) {
        String updateQuery = "UPDATE filter_for_ads SET latitude = ?, last_update_time = ? WHERE id = ?";
        jdbcTemplate.update(updateQuery, latitude,  LocalDateTime.now(), idOfRecord);
    }

    public void updateLongitude(Long idOfRecord, Double longitude) {
        String updateQuery = "UPDATE filter_for_ads SET longitude = ?, last_update_time = ? WHERE id = ?";
        jdbcTemplate.update(updateQuery, longitude, LocalDateTime.now(), idOfRecord);
    }

    public long insertNewRecord(Long userId) {
        String insertQuery = "INSERT INTO filter_for_ads (user_id, last_update_time) VALUES (?, ?)";
        jdbcTemplate.update(insertQuery, userId, LocalDateTime.now());

        return findRecordIdByUserId(userId);
    }

    public boolean isDatabaseCreated() {
        String selectQuery = "SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME='filter_for_ads'";
        return !jdbcTemplate.queryForList(selectQuery).isEmpty();
    }

    public Filter getRecordById(Long id) {
        if (id == null) return null;

        String selectQuery = "SELECT start_date as startDate, end_date as endDate, user_id as userId, " +
                "city as city, start_price as startPrice, end_price as endPrice, latitude as latitude," +
                "longitude as longitude, distance as distance FROM filter_for_ads WHERE id = ?";
        List<Filter> result = jdbcTemplate.query(selectQuery, (rs, rowNum) -> {
            Filter filter = new Filter();
            filter.setLowerDate(rs.getDate("startDate") == null ? null :
                    rs.getDate("startDate").toLocalDate());
            filter.setUpperDate(rs.getDate("endDate") == null ? null :
                    rs.getDate("endDate").toLocalDate());
            filter.setCity(rs.getString("city") == null ? null : rs.getString("city"));
            filter.setUserId(rs.getLong("userId"));
            filter.setLowerPrice(rs.getObject("startPrice", Long.class) == null ? null :
                    rs.getObject("startPrice", Long.class));
            filter.setUpperPrice(rs.getObject("endPrice", Long.class) == null ? null :
                    rs.getObject("endPrice", Long.class));

            filter.setLatitude(rs.getObject("latitude", Double.class) == null ? null :
                    rs.getObject("latitude", Double.class));
            filter.setLongitude(rs.getObject("longitude", Double.class) == null ? null :
                    rs.getObject("longitude", Double.class));
            filter.setDistance(rs.getObject("distance", Double.class) == null ? null :
                    rs.getObject("distance", Double.class));
            return filter;
        }, id);
        return result.isEmpty() ? null : result.get(0);
    }
}
