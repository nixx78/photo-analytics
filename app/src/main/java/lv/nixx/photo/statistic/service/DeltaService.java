package lv.nixx.photo.statistic.service;

import com.google.cloud.Timestamp;
import lombok.AllArgsConstructor;
import lv.nixx.photo.statistic.domain.Delta;
import lv.nixx.photo.statistic.domain.StatisticSnapshot;
import lv.nixx.photo.statistic.domain.Statistics;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
public class DeltaService {

    private final DeltaCalculator deltaCalculator;
    private final StorageService storageService;

    public Delta calculate() {

        List<StatisticSnapshot> lastSnapshots = storageService.getLastWithLimit(5);

        StatisticSnapshot s1 = lastSnapshots.get(0);
        StatisticSnapshot s2 = lastSnapshots.get(1);

        Collection<Statistics> statistics = deltaCalculator.calculate(s1.statistics(), s2.statistics());

        LocalDateTime t1 = toLocalDateTime(s1.createdAt());
        LocalDateTime t2 = toLocalDateTime(s2.createdAt());

        long views = statistics.stream()
                .filter(Objects::nonNull)
                .mapToLong(Statistics::views)
                .sum();

        long likes = statistics.stream()
                .filter(Objects::nonNull)
                .mapToLong(Statistics::likes)
                .sum();


        return Delta.builder()
                .timestamp1(t1)
                .timestamp2(t2)
                .statistics(statistics)
                .diff(getTimeDifference(t1, t2))
                .views(views)
                .likes(likes)
                .build();
    }


    private static LocalDateTime toLocalDateTime(Timestamp timestamp) {

        Instant instant = Instant.ofEpochSecond(
                timestamp.getSeconds(),
                timestamp.getNanos()
        );

        return LocalDateTime.ofInstant(
                instant,
                ZoneId.systemDefault()
        );
    }

    private static String getTimeDifference(LocalDateTime timestamp1, LocalDateTime timestamp2) {
        Duration duration = Duration.between(timestamp1, timestamp2);

        long totalMinutes = duration.toMinutes();

        long days = Math.abs(totalMinutes / (60 * 24));
        long hours = Math.abs((totalMinutes % (60 * 24)) / 60);
        long minutes = Math.abs(totalMinutes % 60);

        return String.format("%d days %d hours %d minutes", days, hours, minutes);
    }

}
