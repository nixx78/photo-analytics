package lv.nixx.photo.statistic.collector;

import com.google.cloud.firestore.*;
import lv.nixx.photo.statistic.collector.domain.Snapshot;
import lv.nixx.photo.statistic.collector.domain.Statistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;

public class StorageService {

    private static final Logger log = LoggerFactory.getLogger(StorageService.class);

    private static final String STATISTICS_SNAPSHOTS = "statistics.snapshots";

    FirestoreOptions firestoreOptions = FirestoreOptions.newBuilder()
            .setDatabaseId("sandbox")
            .build();

    public void saveStatistics(Collection<Statistics> statistics) {

        Firestore db = firestoreOptions.getService();

        Instant now = Instant.now();
        String key = String.valueOf(now.toEpochMilli());

        DocumentReference docRef = db.collection(STATISTICS_SNAPSHOTS).document(key);

        Snapshot data = new Snapshot(
                com.google.cloud.Timestamp.now(),
                new ArrayList<>(statistics)
        );

        try {
            WriteResult result = docRef.set(data).get();
            log.info("Update time [{}]", result.getUpdateTime());
        } catch (Exception ex) {
            log.error("Error  saving statistics", ex);
        }
    }



}
