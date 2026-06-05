package lv.nixx.photo.statistic.domain;

import java.time.LocalDateTime;
import java.util.List;

public record StatisticSnapshotHolder(
        String key,
        LocalDateTime createdAt,
        List<Statistics> statistics
) {
}
