package lv.nixx.photo.statistic.domain;

import com.google.cloud.Timestamp;
import java.util.List;

public record StatisticSnapshot(
        Timestamp createdAt,
        List<Statistics> statistics
) {
}