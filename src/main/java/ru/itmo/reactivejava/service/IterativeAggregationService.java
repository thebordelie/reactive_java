package ru.itmo.reactivejava.service;

import ru.itmo.reactivejava.model.Event;
import ru.itmo.reactivejava.pool.SimplePool;

public class IterativeAggregationService implements AggregationService<EventStatistics, Event> {

    @Override
    public EventStatistics getStatistics(SimplePool<Event> events) {
        int totalEvents = events.size();
        long totalMembers = 0;
        long totalUsers = 0;
        int maxCapacity = Integer.MIN_VALUE;
        int minCapacity = Integer.MAX_VALUE;
        double avgCapacity;
        int capacity = 0;
        for (Event event : events) {
            totalMembers += event.getMembers().size();
            totalUsers += event.getUsers().size();
            maxCapacity = Math.max(maxCapacity, event.getPlacement().getCapacity());
            minCapacity = Math.min(minCapacity, event.getPlacement().getCapacity());
            capacity += event.getPlacement().getCapacity();
        }
        double avgMembersPerEvent = (double) totalMembers / (double) totalEvents;
        avgCapacity = (double) capacity / (double) totalEvents;
        return EventStatistics.builder()
                .totalEvents(totalEvents)
                .totalMembers(totalMembers)
                .totalUsers(totalUsers)
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
