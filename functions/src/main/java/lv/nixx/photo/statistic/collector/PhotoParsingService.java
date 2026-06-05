package lv.nixx.photo.statistic.collector;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Comparator;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PhotoParsingService {

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

    public NavigableMap<String, Integer> parse(String html) {
        Document doc = Jsoup.parse(html);
        NavigableMap<String, Integer> result = createSortedMapInstance();

        Elements items = doc.select("div.grid-item");

        for (Element item : items) {
            Element link = item.selectFirst("a[photo-id]");
            Element likeSpan = item.selectFirst("div.countLike span.count");

            if (link == null || likeSpan == null) {
                continue;
            }

            String photoId = link.attr("photo-id");
            String likesText = likeSpan.text();

            try {
                int likes = Integer.parseInt(likesText.trim());
                result.put(photoId, likes);
            } catch (NumberFormatException ignored) {
            }
        }

        return result;
    }

    public NavigableMap<String, Integer> parseNextPage(String originalResponse) {
        if (originalResponse == null || originalResponse.isEmpty()) {
            return new TreeMap<>();
        }

        var html = originalResponse
                .replace("\\\"", "\"")
                .replace("\\n", "")
                .replace("\\t", "");

        Document doc = Jsoup.parse(html);
        NavigableMap<String, Integer> result = createSortedMapInstance();

        Elements likes = doc.select("div.countLike[photo-id]");

        for (Element like : likes) {
            String photoId = like.attr("photo-id");
            Element countElement = like.selectFirst("span.count");

            if (countElement == null) {
                continue;
            }

            String countText = countElement.text().trim();

            try {
                int count = Integer.parseInt(countText);
                result.put(photoId, count);
            } catch (NumberFormatException ignored) {
            }
        }

        return result;
    }

    public int extractViewsCount(String photoHtml) {
        if (photoHtml == null || photoHtml.isEmpty()) {
            return 0;
        }

        Pattern pattern = Pattern.compile("\"photo_see\":(\\d+)");
        Matcher matcher = pattern.matcher(photoHtml);

        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        }
        return 0;
    }

    private static NavigableMap<String, Integer> createSortedMapInstance() {
        return new TreeMap<>(PHOTO_ID_COMPARATOR);
    }
}