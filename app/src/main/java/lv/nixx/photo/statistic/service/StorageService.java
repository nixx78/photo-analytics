package lv.nixx.photo.statistic.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import lv.nixx.photo.statistic.domain.StatisticSnapshot;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class StorageService {

    private static final String STATISTICS_SNAPSHOTS = "statistics.snapshots";

    FirestoreOptions firestoreOptions = FirestoreOptions.newBuilder()
            .setDatabaseId("sandbox")
            .build();

    public Collection<StatisticSnapshot> getAllStatistics() {
        Firestore db = firestoreOptions.getService();

        try {

            ApiFuture<QuerySnapshot> future =
                    db.collection(STATISTICS_SNAPSHOTS)
                            .orderBy("createdAt")
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
