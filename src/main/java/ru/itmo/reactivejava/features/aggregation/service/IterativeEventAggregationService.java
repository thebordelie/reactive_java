package ru.itmo.reactivejava.features.aggregation.service;

import ru.itmo.reactivejava.domain.event.Event;
import ru.itmo.reactivejava.domain.event.MusicCompetitionGenre;
import ru.itmo.reactivejava.features.aggregation.collector.EventStatisticsAccumulator;
import ru.itmo.reactivejava.features.aggregation.viewmodel.EventStatistics;
import ru.itmo.reactivejava.features.pool.Pools;
import ru.itmo.reactivejava.features.pool.SimplePool;

import java.util.EnumMap;
import java.util.Map;

public class IterativeEventAggregationService implements EventAggregationService {

    SimplePool<Event> events = Pools.get(Event.class);


    @Override
    public Map<MusicCompetitionGenre, EventStatistics> getStatisticsByGenre() {
        EnumMap<MusicCompetitionGenre, EventStatisticsAccumulator> accMap = new EnumMap<>(MusicCompetitionGenre.class);
        for (Event event : events) {
            EventStatisticsAccumulator acc = accMap.computeIfAbsent(event.getDescription().genre(), g -> new EventStatisticsAccumulator());
            acc.update(event);
        }
        EnumMap<MusicCompetitionGenre, EventStatistics> result = new EnumMap<>(MusicCompetitionGenre.class);
        for (Map.Entry<MusicCompetitionGenre, EventStatisticsAccumulator> entry : accMap.entrySet()) {
            result.put(entry.getKey(), entry.getValue().toEventStatistics());
        }
        return result;
    }

    @Override
    public String toString() {
        return "Цикл";
    }

}
