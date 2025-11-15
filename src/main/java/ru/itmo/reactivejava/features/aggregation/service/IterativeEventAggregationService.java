package ru.itmo.reactivejava.features.aggregation.service;

import ru.itmo.reactivejava.domain.event.Event;
import ru.itmo.reactivejava.domain.event.MusicCompetitionGenre;
import ru.itmo.reactivejava.features.aggregation.viewmodel.EventStatistics;
import ru.itmo.reactivejava.features.pool.Pools;
import ru.itmo.reactivejava.features.pool.SimplePool;

import java.util.EnumMap;
import java.util.Map;

public class IterativeEventAggregationService implements EventAggregationService {

    SimplePool<Event> events = Pools.get(Event.class);

    static private class Acc {
        private int totalEvents = 0;
        private long totalMembers = 0;
        private int maxCapacity = Integer.MIN_VALUE;
        private int minCapacity = Integer.MAX_VALUE;
        private int capacity = 0;

        private void update(Event event) {
            totalEvents++;
            totalMembers += event.getMembers().size();
            int placementCapacity = event.getPlacement().getCapacity();
            maxCapacity = Math.max(maxCapacity, placementCapacity);
            minCapacity = Math.min(minCapacity, placementCapacity);
            capacity += placementCapacity;
        }

        private EventStatistics toEventStatistics() {
            double avgMembersPerEvent = 0;
            double avgCapacity = 0;
            if (totalEvents > 0) {
                avgMembersPerEvent = (double) totalMembers / (double) totalEvents;
                avgCapacity = (double) capacity / (double) totalEvents;
            }
            return EventStatistics.builder()
                    .totalEvents(totalEvents)
                    .totalMembers(totalMembers)
                    .avgMembersPerEvent(avgMembersPerEvent)
                    .maxCapacity(maxCapacity)
                    .minCapacity(minCapacity)
                    .avgCapacity(avgCapacity)
                    .build();
        }
    }

    @Override
    public EventStatistics getStatistics() {
        Acc acc = new Acc();
        for (Event event : events) {
            acc.update(event);
        }
        return acc.toEventStatistics();
    }


    @Override
    public Map<MusicCompetitionGenre, EventStatistics> getStatisticsByGenre() {
        EnumMap<MusicCompetitionGenre, Acc> accMap = new EnumMap<>(MusicCompetitionGenre.class);
        for (Event event : events) {
            Acc acc = accMap.computeIfAbsent(event.getDescription().genre(), g -> new Acc());
            acc.update(event);
        }
        EnumMap<MusicCompetitionGenre, EventStatistics> result = new EnumMap<>(MusicCompetitionGenre.class);
        for (Map.Entry<MusicCompetitionGenre, Acc> entry : accMap.entrySet()) {
            result.put(entry.getKey(), entry.getValue().toEventStatistics());
        }
        return result;
    }

    @Override
    public String toString() {
        return "Цикл";
    }

}
