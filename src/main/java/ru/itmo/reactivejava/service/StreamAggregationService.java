package ru.itmo.reactivejava.service;

import ru.itmo.reactivejava.model.Event;
import ru.itmo.reactivejava.pool.SimplePool;
import ru.itmo.reactivejava.utils.collector.EventStatisticsCollector;

public class StreamAggregationService implements AggregationService<EventStatistics, Event> {

    @Override
    public EventStatistics getStatistics(SimplePool<Event> events) {

        return events.stream()
                .collect(new EventStatisticsCollector());
    }
}
