package Util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

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
        LocalDateTime dateTime = LocalDateTime.parse(dateTimeString, FORMATTER);
        return dateTime.atZone(ZONE).toEpochSecond();
    }
}