package ru.itmo.reactivejava.service;

import ru.itmo.reactivejava.model.Event;
import ru.itmo.reactivejava.pool.Pools;
import ru.itmo.reactivejava.pool.SimplePool;
import ru.itmo.reactivejava.utils.collector.EventStatisticsCollector;

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
