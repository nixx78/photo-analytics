package lv.nixx.photo.statistic.collector.function;

import com.google.cloud.functions.HttpFunction;
import com.google.cloud.functions.HttpRequest;
import com.google.cloud.functions.HttpResponse;
import com.google.gson.Gson;
import lv.nixx.photo.statistic.collector.PhotoDataFetcher;
import lv.nixx.photo.statistic.collector.PhotoParsingService;
import lv.nixx.photo.statistic.collector.PhotoStatisticCollector;
import lv.nixx.photo.statistic.collector.StorageService;
import lv.nixx.photo.statistic.collector.domain.Timing;

import java.io.PrintWriter;
import java.net.http.HttpClient;
import java.time.Duration;

public class PhotoStatsFunction implements HttpFunction {

    private final PhotoStatisticCollector collector;
    private final Gson gson = new Gson();

    public PhotoStatsFunction() {
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
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        Timing timing = collector.collectStatistics();
        response.appendHeader("Content-Type", "application/json");
        try (PrintWriter writer = new PrintWriter(response.getWriter())) {
            writer.write(gson.toJson(timing));
        }
    }
}