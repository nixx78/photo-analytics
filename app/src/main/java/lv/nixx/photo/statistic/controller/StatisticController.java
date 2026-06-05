package lv.nixx.photo.statistic.controller;

import lombok.AllArgsConstructor;
import lv.nixx.photo.statistic.domain.Delta;
import lv.nixx.photo.statistic.domain.StatisticSnapshot;
import lv.nixx.photo.statistic.service.DeltaService;
import lv.nixx.photo.statistic.service.StorageService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Collection;

@RestController
@RequestMapping("/api/statistics")
@AllArgsConstructor
public class StatisticController {

    private final StorageService storageService;
    private final DeltaService deltaCalculator;

    @GetMapping("/all")
    public Collection<StatisticSnapshot> getAllStatistics() {
        return storageService.getAllStatistics();
    }

    @GetMapping("/difference")
    public Delta getStatisticsDelta() {
        return deltaCalculator.calculate();
    }

    @GetMapping("/synch_time")
    public Collection<LocalDateTime> getSynchTimeStatistics() {
        return storageService.getSynchTiming();
    }

}