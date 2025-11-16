package ru.itmo.reactivejava.features.aggregation.internal;

import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.itmo.reactivejava.domain.event.Event;
import ru.itmo.reactivejava.features.aggregation.viewmodel.EventStatistics;

@Getter
@NoArgsConstructor
public class EventStatisticsAccumulator {
    private int totalEvents = 0;
    private long totalMembers = 0;
    private int maxCapacity = Integer.MIN_VALUE;
    private int minCapacity = Integer.MAX_VALUE;
    private int capacity = 0;

    public void update(Event event) {
        int placementCapacity = event.getPlacement().getCapacity();
        totalEvents++;
        totalMembers += event.getMembers().size();
        maxCapacity = Math.max(maxCapacity, placementCapacity);
        minCapacity = Math.min(minCapacity, placementCapacity);
        capacity += placementCapacity;
    }

    public EventStatisticsAccumulator merge(EventStatisticsAccumulator other) {
        this.totalEvents += other.totalEvents;
        this.totalMembers += other.totalMembers;
        this.capacity += other.capacity;
        this.minCapacity = Math.min(this.minCapacity, other.minCapacity);
        this.maxCapacity = Math.max(this.maxCapacity, other.maxCapacity);
        return this;
    }

    public EventStatistics toEventStatistics() {
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
