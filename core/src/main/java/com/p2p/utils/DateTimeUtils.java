package com.p2p.utils;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;

import com.p2p.exceptions.CoreException;
import org.springframework.stereotype.Component;

@Component
public class DateTimeUtils {

    public static final String DATE_YY_MM_DD_FORMAT = "yyyy-MM-DD";
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    public static final ZoneId ASIA_JERUSALEM_ZONE_ID = ZoneId.of("Asia/Jerusalem");
    public static final LocalDateTime DEFAULT_DATE_TIME = LocalDateTime.of(2018, 12, 10, 12, 00);

    public LocalDate getDateFromString(String dateString) {
        try {
            LocalDate localDate = LocalDate.parse(dateString, DATE_FORMATTER);
            return localDate;
        } catch (DateTimeParseException de) {
            throw new CoreException.NotValidException(String.format("Date must be of format %s", DATE_YY_MM_DD_FORMAT));
        }
    }

    public LocalDateTime getTimeFromString(String dateTimeString) {
        try {
            LocalDateTime localDateTime = LocalDateTime.parse(dateTimeString, DATE_TIME_FORMATTER);
            return localDateTime;
        } catch (DateTimeParseException de) {
            throw new CoreException.NotValidException(
                    String.format("Time must be of format %s", DATE_TIME_FORMATTER.toString()), de);
        }
    }

    public String convertTimeToString(LocalDateTime dateTime) {
        return convertTimeToString(dateTime, DATE_TIME_FORMATTER);
    }

    public String convertTimeToString(LocalDateTime dateTime, DateTimeFormatter dateTimeFormatter) {
        try {
            return dateTime.format(dateTimeFormatter);
        } catch (Exception e) {
            throw new CoreException.NotValidException(e.getMessage(), e);
        }
    }

    public String convertTimeToString(ZonedDateTime dateTime) {
        return convertTimeToString(dateTime, DATE_TIME_FORMATTER);
    }

    public String convertTimeToString(ZonedDateTime dateTime, DateTimeFormatter dateTimeFormatter) {
        try {
            return dateTime.format(dateTimeFormatter);
        } catch (Exception e) {
            throw new CoreException.NotValidException(e.getMessage(), e);
        }
    }

    public ZonedDateTime getZonedDateTime(LocalDateTime dateTime) {
        return getZonedDateTime(dateTime, ASIA_JERUSALEM_ZONE_ID);
    }

    public LocalDateTime getApplicationCurrentTime() {
        ZonedDateTime dateTime = ZonedDateTime.now(ASIA_JERUSALEM_ZONE_ID);
        return dateTime.toLocalDateTime();
    }

    public ZonedDateTime getZonedDateTime(LocalDateTime dateTime, ZoneId zoneId) {
        ZonedDateTime zonedDateTime = ZonedDateTime.of(dateTime, zoneId);
        return zonedDateTime;
    }

    public ZonedDateTime getDateTimeFromDate(LocalDate date, boolean startOfDay) {
        ZoneId zoneId = ASIA_JERUSALEM_ZONE_ID;
        if (startOfDay) {
            return date.atStartOfDay(zoneId);
        } else {
            return date.atTime(LocalTime.MAX).atZone(zoneId);
        }
    }

    public ZonedDateTime getZonedDateTimeFromString(String dateTimeString) {
        try {
            return ZonedDateTime.parse(dateTimeString);
        } catch (Exception e) {
            throw new CoreException.NotValidException(e.getMessage(), e);
        }
    }

    public LocalDateTime getEndTimeOfLocalDate(LocalDate localDate) {
        return localDate.atTime(LocalTime.MAX);
    }

    public LocalDateTime getStartTimeOfLocalDate(LocalDate localDate) {
        return localDate.atStartOfDay();
    }

    public ZonedDateTime getEndTimeOfZonedDateTime(ZonedDateTime zonedDateTime) {
        return zonedDateTime.toLocalDate().atTime(LocalTime.MAX).atZone(ASIA_JERUSALEM_ZONE_ID);
    }

    public ZonedDateTime getStartTimeOfZonedDateTime(ZonedDateTime zonedDateTime) {
        return zonedDateTime.toLocalDate().atStartOfDay(ASIA_JERUSALEM_ZONE_ID);
    }

    public LocalDateTime getLocalTimeFromLocalDate(LocalDate localDate, boolean atStartOfDay) {
        if (atStartOfDay) {
            return getStartTimeOfLocalDate(localDate);
        }
        return getEndTimeOfLocalDate(localDate);
    }

    public long between(Temporal startTime, Temporal endTime, ChronoUnit unit) {
        return unit.between(startTime, endTime);
    }

    public LocalTime getLocalTime(String localTimeString) {
        try {
            return LocalTime.parse(localTimeString);
        } catch (Exception e) {
            throw new CoreException.NotValidException(e.getMessage(), e);
        }
    }

    public LocalDateTime copyTimeToLocalDateTime(LocalDateTime localDateTime, LocalTime localTime) {
        return localTime.atDate(localDateTime.toLocalDate());
    }

    public long getEpochSeconds(LocalDateTime localDateTime) {
        return localDateTime.atZone(ASIA_JERUSALEM_ZONE_ID).toEpochSecond();
    }

    public String formatLocalDateTime(LocalDateTime localDateTime, String format) {
        return formatLocalDateTime(localDateTime, DateTimeFormatter.ofPattern(format));
    }

    public String formatLocalDateTime(LocalDateTime localDateTime, DateTimeFormatter dateTimeFormatter) {
        return localDateTime.format(dateTimeFormatter);
    }

    public String formatLocalDate(LocalDate localDate, String format) {
        return formatLocalDate(localDate, DateTimeFormatter.ofPattern(format));
    }

    public String formatLocalDate(LocalDate localDate, DateTimeFormatter dateTimeFormatter) {
        return localDate.format(dateTimeFormatter);
    }

    public Duration getDurationBetween(Temporal startTime, Temporal endTime) {
        return Duration.between(startTime, endTime);
    }

    public String formatDuration(Duration duration) {
        return duration.toString();
    }
}
