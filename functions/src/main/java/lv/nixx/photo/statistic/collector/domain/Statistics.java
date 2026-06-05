package lv.nixx.photo.statistic.collector.domain;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Statistics{
        private String photoId;
        private int likes;
        private int views;
}