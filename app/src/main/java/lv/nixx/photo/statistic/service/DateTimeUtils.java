package lv.nixx.photo.statistic.service;

import com.google.cloud.Timestamp;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class DateTimeUtils {

    private DateTimeUtils() {
    }

    public static LocalDateTime toLocalDateTime(Timestamp timestamp) {
        return LocalDateTime.ofInstant(
                Instant.ofEpochSecond(
                        timestamp.getSeconds(),
                        timestamp.getNanos()),
                ZoneId.systemDefault());
    }

}
