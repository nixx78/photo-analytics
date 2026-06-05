package lv.nixx.photo.statistic.service;

import lv.nixx.photo.statistic.domain.Statistics;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class DeltaCalculator {

    public Collection<Statistics> calculate(Collection<Statistics> current, Collection<Statistics> previous) {

        Collection<Statistics> cur = current == null ? List.of() : current;
        Collection<Statistics> prev = previous == null ? List.of() : previous;

        Map<String, Statistics> curMap = cur.stream()
                .collect(Collectors.toMap(Statistics::photoId, s -> s));
        Map<String, Statistics> prevMap = prev.stream()
                .collect(Collectors.toMap(Statistics::photoId, s -> s));

        Set<String> allIds = new HashSet<>();
        allIds.addAll(curMap.keySet());
        allIds.addAll(prevMap.keySet());

        List<Statistics> result = new ArrayList<>(allIds.size());

        for (String id : allIds) {
            Statistics cs = curMap.get(id);
            Statistics ps = prevMap.get(id);

            int curLikes = cs == null ? 0 : cs.likes();
            int curViews = cs == null ? 0 : cs.views();

            int prevLikes = ps == null ? 0 : ps.likes();
            int prevViews = ps == null ? 0 : ps.views();

            int likesDelta = curLikes - prevLikes;
            int viewsDelta = curViews - prevViews;

            if (likesDelta != 0 || viewsDelta != 0) {
                result.add(new Statistics(id, likesDelta, viewsDelta));
            }
        }

        Comparator<String> idComparator = Comparator
                .comparingLong((String s) -> {
                    if (s == null) return Long.MAX_VALUE;
                    try {
                        return Long.parseLong(s);
                    } catch (NumberFormatException ex) {
                        return Long.MAX_VALUE;
                    }
                }).reversed()
                .thenComparing(Comparator.nullsFirst(Comparator.reverseOrder()));

        result.sort(Comparator.comparing(Statistics::photoId, idComparator));

        return result;
    }
}
