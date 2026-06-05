package lv.nixx.photo.statistic.collector;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class PhotoDataFetcher {

    private final HttpClient httpClient;
    private static final String BASE_URL = "https://35photo.pro";
    private final Duration timeout;

    public PhotoDataFetcher(HttpClient httpClient, Duration timeout) {
        this.httpClient = httpClient;
        this.timeout =  Duration.ofSeconds(20);
    }

    private String get(String url) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .timeout(timeout)
//                    .header("User-Agent", "JavaHttpClient")
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return response.body();
        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Error fetching url: " + url, e);
        }
    }

    public String fetchUserProfile(String username) {
        return get(BASE_URL + "/" + username);
    }

    public String fetchNextPageData(String lastPhotoId, String userId) {
        String url = BASE_URL + "/show_block.php?type=getNextPageData&page=photoUser&lastId=%s&user_id=%s"
                .formatted(lastPhotoId, userId);
        return get(url);
    }

    public String fetchPhotoDetails(String photoId) {
        return get(BASE_URL + "/photo_" + photoId);
    }
}