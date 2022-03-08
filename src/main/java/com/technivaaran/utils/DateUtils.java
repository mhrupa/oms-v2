package com.technivaaran.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class DateUtils {
    private DateUtils(){}

    public static LocalDateTime getCurrentDateTime() {
        return LocalDateTime.now();
    }

    public static LocalDate getCurrentDate() {
        return LocalDate.now();
    }
}
