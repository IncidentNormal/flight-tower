package com.duncantait.flights.helper;

import java.time.LocalDateTime;

public class LocalDateTimeHelper {

    public static LocalDateTime toLocalDateTime(String timestamp) {
        try {
            return LocalDateTime.parse(timestamp);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid timestamp: " + timestamp);
        }
    }
}
