package lv.nixx.photo.statistic.collector.function;


import com.google.cloud.functions.CloudEventsFunction;
import io.cloudevents.CloudEvent;
import lv.nixx.photo.statistic.collector.PhotoDataFetcher;
import lv.nixx.photo.statistic.collector.PhotoParsingService;
import lv.nixx.photo.statistic.collector.PhotoStatisticCollector;
import lv.nixx.photo.statistic.collector.StorageService;
import lv.nixx.photo.statistic.collector.domain.Timing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.http.HttpClient;
import java.time.Duration;

public class PhotoStatsFunctionPubSub implements CloudEventsFunction {

    private static final Logger log = LoggerFactory.getLogger(PhotoStatsFunctionPubSub.class);

    private final PhotoStatisticCollector collector;

    public PhotoStatsFunctionPubSub() {
        HttpClient httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(20))
                .build();

        PhotoDataFetcher fetcher = new PhotoDataFetcher(httpClient, Duration.ofSeconds(20));
        PhotoParsingService parser = new PhotoParsingService();

        StorageService storage = new StorageService();

        String username = System.getenv().getOrDefault("SIGHT_USER_NAME", "nixx");

        this.collector = new PhotoStatisticCollector(fetcher, parser, username, storage);
    }

    @Override
    public void accept(CloudEvent event) {
        Timing timing = collector.collectStatistics();

        log.info("Photo statistics collected {}", timing);
    }

}