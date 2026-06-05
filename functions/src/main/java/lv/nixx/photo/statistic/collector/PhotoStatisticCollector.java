package lv.nixx.photo.statistic.collector;

import lv.nixx.photo.statistic.collector.domain.Statistics;
import lv.nixx.photo.statistic.collector.domain.Timing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.TimeUnit;

import java.util.stream.Collectors;

public class PhotoStatisticCollector {

    private static final Logger log = LoggerFactory.getLogger(PhotoStatisticCollector.class);

    private final PhotoDataFetcher dataFetchService;
    private final PhotoParsingService parsingService;
    private final String username;
    private final StorageService storageService;
    private final String userId;
    private final int delayMs;
    private final int maxPhotos;

    private static final Comparator<String> PHOTO_ID_COMPARATOR = Comparator
            .comparingLong((String s) -> {
                if (s == null) return Long.MAX_VALUE;
                try {
                    return Long.parseLong(s);
                } catch (NumberFormatException ex) {
                    return Long.MAX_VALUE;
                }
            }).reversed()
            .thenComparing(Comparator.nullsFirst(Comparator.reverseOrder()));

    public PhotoStatisticCollector(PhotoDataFetcher dataFetchService,
                                   PhotoParsingService parsingService,
                                   String username,
                                   StorageService storageService) {
        this.dataFetchService = dataFetchService;
        this.parsingService = parsingService;
        this.username = username;
        this.storageService = storageService;
        this.userId = "1130375";
        //FIXME Попробовать поставить меньше, нужно уложится в 500 секунд
        this.delayMs = 500;
        this.maxPhotos = Integer.MAX_VALUE;
    }

    public Timing collectStatistics() {
        long start = System.currentTimeMillis();

        log.info("Starting to parse photos, user name {}", username);

        String html = dataFetchService.fetchUserProfile(username);
        NavigableMap<String, Integer> pages = parsingService.parse(html);

        String lastPhotoId = pages.isEmpty() ? null : pages.lastKey();

        if (lastPhotoId != null && !lastPhotoId.isBlank()) {
            NavigableMap<String, Integer> next = fetchAndParseNextPage(lastPhotoId);

            while (!next.isEmpty()) {
                pages.putAll(next);
                lastPhotoId = next.lastKey();
                next = fetchAndParseNextPage(lastPhotoId);
            }
        }

        log.info("Total photo count [" + pages.size() + "]");

        Collection<Statistics> statistics = processPhotosStatistics(pages);

        if (storageService != null) {
            storageService.saveStatistics(statistics);
        }

        return new Timing(statistics.size(), System.currentTimeMillis() - start);
    }

    private NavigableMap<String, Integer> fetchAndParseNextPage(String lastPhotoId) {
        String response = dataFetchService.fetchNextPageData(lastPhotoId, userId);
        return parsingService.parseNextPage(response);
    }

    private Collection<Statistics> processPhotosStatistics(Map<String, Integer> photos) {

        Map<String, Statistics> byId = photos.entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> new Statistics(entry.getKey(), entry.getValue(), 0)));

        long count = 0;

        for (Map.Entry<String, Integer> entry : photos.entrySet()) {
            count++;

            String photoId = entry.getKey();

            String photoHtml = dataFetchService.fetchPhotoDetails(photoId);
            int views = parsingService.extractViewsCount(photoHtml);

            // log.info("#" + count + " - " + likes + " " + views + " " + "https://35photo.pro/photo_" + photoId);

            byId.computeIfPresent(photoId, (id, stats) -> new Statistics(id, stats.getLikes(), views));

            try {
                TimeUnit.MILLISECONDS.sleep(delayMs);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }

            if (count >= maxPhotos) {
                break;
            }
        }

        Collection<Statistics> result = new TreeSet<>(
                Comparator.comparing(Statistics::getPhotoId, PHOTO_ID_COMPARATOR)
        );
        result.addAll(byId.values());

        return result;
    }
}