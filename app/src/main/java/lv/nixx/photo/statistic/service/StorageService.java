package lv.nixx.photo.statistic.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import lv.nixx.photo.statistic.domain.StatisticSnapshot;
import lv.nixx.photo.statistic.domain.StatisticSnapshotHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

@Service
public class StorageService {

    private static final String STATISTICS_SNAPSHOTS = "statistics.snapshots";
    private static final Comparator<StatisticSnapshotHolder> STATISTIC_SNAPSHOT_HOLDER_COMPARATOR =   Comparator.comparing(StatisticSnapshotHolder::createdAt).reversed();

    FirestoreOptions firestoreOptions = FirestoreOptions.newBuilder()
            .setDatabaseId("sandbox")
            .build();

    public Collection<StatisticSnapshotHolder> getAllStatistics() {
        Firestore db = firestoreOptions.getService();

        try {

            ApiFuture<QuerySnapshot> future =
                    db.collection(STATISTICS_SNAPSHOTS)
                            .orderBy("createdAt")
                            .get();

            QuerySnapshot snapshot = future.get();
            List<QueryDocumentSnapshot> docs = snapshot.getDocuments();

            List<StatisticSnapshotHolder> result = new ArrayList<>();

            for (QueryDocumentSnapshot doc : docs) {
                String id = doc.getId();
                StatisticSnapshot s = doc.toObject(StatisticSnapshot.class);
                LocalDateTime createdAt = DateTimeUtils.toLocalDateTime(s.createdAt());

                result.add(new StatisticSnapshotHolder(id, createdAt, s.statistics()));
            }

            result.sort(STATISTIC_SNAPSHOT_HOLDER_COMPARATOR);

            return result;

        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public List<StatisticSnapshot> getLastWithLimit(int limit) {
        Firestore db = firestoreOptions.getService();

        try {

            ApiFuture<QuerySnapshot> future =
                    db.collection(STATISTICS_SNAPSHOTS)
                            .orderBy("createdAt", Query.Direction.DESCENDING)
                            .limit(limit)
                            .get();

            QuerySnapshot snapshot = future.get();
            List<QueryDocumentSnapshot> docs = snapshot.getDocuments();

            List<StatisticSnapshot> result = new ArrayList<>();

            for (QueryDocumentSnapshot doc : docs) {
                result.add(doc.toObject(StatisticSnapshot.class));
            }

            return result;

        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public Collection<LocalDateTime> getSynchTiming() {


        return null;
    }
}
