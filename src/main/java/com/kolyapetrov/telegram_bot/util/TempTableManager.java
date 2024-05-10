package com.kolyapetrov.telegram_bot.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class TempTableManager {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public TempTableManager(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void createTempTable() {
        String createTableQuery = "CREATE TEMP TABLE IF NOT EXISTS selected_dates " +
                "(id SERIAL PRIMARY KEY, start_date DATE, end_date DATE, user_id int8, order_id int8)";
        jdbcTemplate.execute(createTableQuery);
    }

    public void dropTempTable() {
        String dropTableQuery = "DROP TABLE IF EXISTS selected_dates";
        jdbcTemplate.execute(dropTableQuery);
    }

    public void updateStartDate(Long id, String startDate) {
        LocalDate date = (startDate == null) ? null : LocalDate.parse(startDate);
        String updateQuery = "UPDATE selected_dates SET start_date = ? WHERE id = ?";
        jdbcTemplate.update(updateQuery, date, id);
    }

    public void updateEndDate(Long id, String endDate) {
        LocalDate date = (endDate == null) ? null : LocalDate.parse(endDate);
        String updateQuery = "UPDATE selected_dates SET end_date = ? WHERE id = ?";
        jdbcTemplate.update(updateQuery, date, id);
    }

    public void insertDate(Long user_id, Long order_id, String start_date) {
        LocalDate date = LocalDate.parse(start_date);
        String insertQuery = "INSERT INTO selected_dates (user_id, order_id, start_date) VALUES (?, ?, ?)";
        jdbcTemplate.update(insertQuery, user_id, order_id, date);
    }

    public Long getRecordId(Long user_id, Long order_id) {
        String selectQuery = "SELECT id FROM selected_dates WHERE user_id = ? AND order_id = ?";
        List<Long> result = jdbcTemplate.queryForList(selectQuery, Long.class, user_id, order_id);
        return result.isEmpty() ? null : result.get(0);
    }

    public LocalDate getStartDateById(Long id) {
        String selectQuery = "SELECT start_date FROM selected_dates WHERE id = ?";
        List<LocalDate> result = jdbcTemplate.queryForList(selectQuery, LocalDate.class, id);
        return result.isEmpty() ? null : result.get(0);
    }

    public LocalDate getEndDateById(Long id) {
        String selectQuery = "SELECT end_date FROM selected_dates WHERE id = ?";
        List<LocalDate> result = jdbcTemplate.queryForList(selectQuery, LocalDate.class, id);
        return result.isEmpty() ? null : result.get(0);
    }

    public BookingTemp getRecordById(Long id) {
        if (id == null) return null;

        String selectQuery = "SELECT start_date as startDate, end_date as endDate, user_id as userId, " +
                "order_id as orderId FROM selected_dates WHERE id = ?";
        List<BookingTemp> result = jdbcTemplate.query(selectQuery, (rs, rowNum) -> {
            BookingTemp bookingTemp = new BookingTemp();
            bookingTemp.setStartDate(rs.getDate("startDate") == null ?
                    null : rs.getDate("startDate").toLocalDate());
            bookingTemp.setEndDate(rs.getDate("endDate") == null ?
                    null : rs.getDate("endDate").toLocalDate());
            bookingTemp.setUserId(rs.getLong("userId"));
            bookingTemp.setOrderId(rs.getLong("orderId"));
            return bookingTemp;
        }, id);
        return result.isEmpty() ? null : result.get(0);
    }

    public void deleteRecordById(Long id) {
        String deleteQuery = "DELETE FROM selected_dates WHERE id = ?";
        jdbcTemplate.update(deleteQuery, id);
    }
}
