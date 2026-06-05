package lv.nixx.photo.statistic.collector;

import lv.nixx.photo.statistic.collector.domain.Timing;

import java.net.http.HttpClient;
import java.time.Duration;

public class MainRunner
{

    private final PhotoStatisticCollector collector;

    public MainRunner() {
        HttpClient httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(20))
                .build();

        PhotoDataFetcher fetcher = new PhotoDataFetcher(httpClient, Duration.ofSeconds(20));
        PhotoParsingService parser = new PhotoParsingService();

        StorageService storage = new StorageService();

        String username = System.getenv().getOrDefault("SIGHT_USER_NAME", "nixx");

        this.collector = new PhotoStatisticCollector(fetcher, parser, username, storage);
    }

    public Timing run() {
        return collector.collectStatistics();
    }

    public static void main(String[] args) {
        Timing run = new MainRunner().run();
        System.out.println(run.toString());

    }
}
