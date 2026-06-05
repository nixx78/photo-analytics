package lv.nixx.photo.statistic.domain;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.Collection;

@Builder
public record Delta(

        LocalDateTime timestamp1,
        LocalDateTime timestamp2,
        String diff,
        Collection<Statistics> statistics,

        long views,
        long likes

) {


}
