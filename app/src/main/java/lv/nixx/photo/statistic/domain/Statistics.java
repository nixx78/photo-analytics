package lv.nixx.photo.statistic.domain;

import lombok.With;

public record Statistics(
        String photoId,
        int likes,
        @With
        int views
) {
}