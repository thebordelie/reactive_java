package ru.itmo.reactivejava.features.aggregation.service;

import ru.itmo.reactivejava.domain.event.Event;
import ru.itmo.reactivejava.domain.event.MusicCompetitionGenre;
import ru.itmo.reactivejava.features.aggregation.collector.ConcurrentEventStatisticsByGenreCollector;
import ru.itmo.reactivejava.features.aggregation.viewmodel.EventStatistics;
import ru.itmo.reactivejava.features.pool.Pools;
import ru.itmo.reactivejava.features.pool.SimplePool;

import java.util.Map;

public class ParallelStreamEventAggregationService implements EventAggregationService {

    SimplePool<Event> events = Pools.get(Event.class);

    @Override
    public Map<MusicCompetitionGenre, EventStatistics> getStatisticsByGenre() {
        return events.parallelStream().collect(new ConcurrentEventStatisticsByGenreCollector());
    }

    @Override
    public String toString() {
        return "Параллельный стрим";
    }
}
