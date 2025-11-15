package ru.itmo.reactivejava.features.aggregation.service;

import ru.itmo.reactivejava.domain.event.Event;
import ru.itmo.reactivejava.domain.event.MusicCompetitionGenre;
import ru.itmo.reactivejava.features.aggregation.collector.ConcurrentEventStatisticsByGenreCollector;
import ru.itmo.reactivejava.features.aggregation.collector.ConcurrentEventStatisticsCollector;
import ru.itmo.reactivejava.features.aggregation.viewmodel.EventStatistics;
import ru.itmo.reactivejava.features.pool.EventSpliterator;
import ru.itmo.reactivejava.features.pool.Pools;
import ru.itmo.reactivejava.features.pool.SimplePool;

import java.util.Map;
import java.util.stream.StreamSupport;

public class OptimizedParallelStreamEventAggregationService implements EventAggregationService {

    SimplePool<Event> events = Pools.get(Event.class);

    @Override
    public EventStatistics getStatistics() {
        EventSpliterator spliterator = new EventSpliterator(events.getValues());
        return StreamSupport.stream(spliterator, true)
                .collect(new ConcurrentEventStatisticsCollector());
    }

    @Override
    public Map<MusicCompetitionGenre, EventStatistics> getStatisticsByGenre() {
        EventSpliterator spliterator = new EventSpliterator(events.getValues());
        return StreamSupport.stream(spliterator, true)
                .collect(new ConcurrentEventStatisticsByGenreCollector());
    }

    @Override
    public String toString() {
        return "Оптимизированный параллельный стрим";
    }
}
