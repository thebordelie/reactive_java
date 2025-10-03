package ru.itmo.reactivejava.features.aggregation.service;

import ru.itmo.reactivejava.domain.event.Event;
import ru.itmo.reactivejava.features.aggregation.viewmodel.EventStatistics;
import ru.itmo.reactivejava.features.pool.Pools;
import ru.itmo.reactivejava.features.pool.SimplePool;

public class IterativeAggregationService implements AggregationService<EventStatistics> {

    SimplePool<Event> events = Pools.get(Event.class);

    @Override
    public EventStatistics getStatistics() {
        int totalEvents = events.size();
        long totalMembers = 0;
        int maxCapacity = Integer.MIN_VALUE;
        int minCapacity = Integer.MAX_VALUE;
        double avgCapacity;
        int capacity = 0;
        for (Event event : events) {
            totalMembers += event.getMembers().size();
            maxCapacity = Math.max(maxCapacity, event.getPlacement().getCapacity());
            minCapacity = Math.min(minCapacity, event.getPlacement().getCapacity());
            capacity += event.getPlacement().getCapacity();
        }
        double avgMembersPerEvent = (double) totalMembers / (double) totalEvents;
        avgCapacity = (double) capacity / (double) totalEvents;
        return EventStatistics.builder()
                .totalEvents(totalEvents)
                .totalMembers(totalMembers)
                .avgMembersPerEvent(avgMembersPerEvent)
                .maxCapacity(maxCapacity)
                .minCapacity(minCapacity)
                .avgCapacity(avgCapacity)
                .build();
    }

    @Override
    public String toString() {
        return "Агрегация через цикл";
    }
}
