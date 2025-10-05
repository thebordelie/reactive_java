package ru.itmo.reactivejava.features.aggregation.service;

import ru.itmo.reactivejava.domain.event.Event;
import ru.itmo.reactivejava.domain.event.MusicCompetitionGenre;
import ru.itmo.reactivejava.features.aggregation.collector.EventStatisticsByGenreCollector;
import ru.itmo.reactivejava.features.aggregation.collector.EventStatisticsCollector;
import ru.itmo.reactivejava.features.aggregation.viewmodel.EventStatistics;
import ru.itmo.reactivejava.features.pool.Pools;
import ru.itmo.reactivejava.features.pool.SimplePool;

import java.util.Map;

public class StreamEventAggregationService implements EventAggregationService {

    SimplePool<Event> events = Pools.get(Event.class);

    @Override
    public EventStatistics getStatistics() {
        return events.stream().collect(new EventStatisticsCollector());
    }

    @Override
    public Map<MusicCompetitionGenre, EventStatistics> getStatisticsByGenre() {
        return events.stream().collect(new EventStatisticsByGenreCollector());
    }

    @Override
    public String toString() {
        return "Кастомный стрим";
    }
}
