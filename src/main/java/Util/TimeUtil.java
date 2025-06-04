package Util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class TimeUtil {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static final ZoneId ZONE = ZoneId.systemDefault();

    // Converts epoch seconds to formatted string
    public static String epochToString(Long epochSeconds) {
        if (epochSeconds == null) return null;
        LocalDateTime dateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(epochSeconds), ZONE);
        return dateTime.format(FORMATTER);
    }

    // Converts formatted string to epoch seconds
    public static Long stringToEpoch(String dateTimeString) {
        if (dateTimeString == null || dateTimeString.isEmpty()) return null;
        try{
            LocalDateTime dateTime = LocalDateTime.parse(dateTimeString, FORMATTER);
            return dateTime.atZone(ZONE).toEpochSecond();
        }catch (DateTimeParseException e1) {
            try {
                // If that fails, try just the date: yyyy-MM-dd
                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate date = LocalDate.parse(dateTimeString, dateFormatter);
                return date.atStartOfDay(ZONE).toEpochSecond();
            } catch (DateTimeParseException e2) {
                // If both fail, throw a clear error
                throw new IllegalArgumentException("Invalid date format: " + dateTimeString + ". Expected formats: yyyy-MM-dd or yyyy-MM-dd HH:mm:ss");
            }
        }
    }
}