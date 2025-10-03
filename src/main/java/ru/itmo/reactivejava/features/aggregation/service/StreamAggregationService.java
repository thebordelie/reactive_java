package ru.itmo.reactivejava.features.aggregation.service;

import ru.itmo.reactivejava.domain.event.Event;
import ru.itmo.reactivejava.features.aggregation.viewmodel.EventStatistics;
import ru.itmo.reactivejava.features.pool.Pools;
import ru.itmo.reactivejava.features.pool.SimplePool;
import ru.itmo.reactivejava.features.aggregation.collector.EventStatisticsCollector;

public class StreamAggregationService implements AggregationService<EventStatistics> {

    SimplePool<Event> events = Pools.get(Event.class);

    @Override
    public EventStatistics getStatistics() {
        return events.stream()
                .collect(new EventStatisticsCollector());
    }

    @Override
    public String toString() {
        return "Агрегация через стрим и кастомный коллектор";
    }
}
