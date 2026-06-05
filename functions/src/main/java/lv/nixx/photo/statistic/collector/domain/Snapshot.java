package lv.nixx.photo.statistic.collector.domain;

import com.google.cloud.Timestamp;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Snapshot {
    private Timestamp createdAt;
    private Collection<Statistics> statistics;
}