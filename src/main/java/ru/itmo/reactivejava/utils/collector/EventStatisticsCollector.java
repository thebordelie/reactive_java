package ru.itmo.reactivejava.utils.collector;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import ru.itmo.reactivejava.model.Event;
import ru.itmo.reactivejava.service.EventStatistics;

import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

public final class EventStatisticsCollector implements Collector<Event, EventStatisticsCollector.EventStatisticsAccumulator, EventStatistics> {


    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static final class EventStatisticsAccumulator {
        public long totalEvents;
        public long totalMembers;
        public long totalCapacity;
        public int minCapacity;
        public int maxCapacity;
    }


    @Override
    public Supplier<EventStatisticsAccumulator> supplier() {
        EventStatisticsAccumulator acc = new EventStatisticsAccumulator();
        acc.totalEvents = 0;
        acc.totalMembers = 0;
        acc.totalCapacity = 0;
        acc.minCapacity = Integer.MAX_VALUE;
        acc.maxCapacity = Integer.MIN_VALUE;
        return () -> acc;
    }

    @Override
    public BiConsumer<EventStatisticsAccumulator, Event> accumulator() {
        return (EventStatisticsAccumulator acc, Event event) -> {
            acc.minCapacity = Math.min(acc.minCapacity, event.getPlacement().getCapacity());
            acc.maxCapacity = Math.max(acc.maxCapacity, event.getPlacement().getCapacity());
            acc.totalCapacity += event.getPlacement().getCapacity();
            acc.totalMembers += event.getMembers().size();
            acc.totalEvents += 1;
        };
    }

    @Override
    public BinaryOperator<EventStatisticsAccumulator> combiner() {
        return (EventStatisticsAccumulator acc1, EventStatisticsAccumulator acc2) -> {
            EventStatisticsAccumulator acc = new EventStatisticsAccumulator();
            acc.totalEvents = acc1.totalEvents + acc2.totalEvents;
            acc.totalMembers = acc1.totalMembers + acc2.totalMembers;
            acc.totalCapacity = acc1.totalCapacity + acc2.totalCapacity;
            acc.minCapacity = Math.min(acc1.minCapacity, acc2.minCapacity);
            acc.maxCapacity = Math.max(acc1.maxCapacity, acc2.maxCapacity);
            return acc;
        };
    }

    @Override
    public Function<EventStatisticsAccumulator, EventStatistics> finisher() {
        return acc -> {
            if (acc.totalEvents == 0) {
                return EventStatistics.builder()
                        .totalMembers(0)
                        .totalEvents(0)
                        .minCapacity(0)
                        .maxCapacity(0)
                        .avgMembersPerEvent(0)
                        .avgCapacity(0)
                        .build();
            }
            return EventStatistics.builder()
                    .totalMembers(acc.totalMembers)
                    .totalEvents(acc.totalEvents)
                    .minCapacity(acc.minCapacity)
                    .maxCapacity(acc.maxCapacity)
                    .avgMembersPerEvent((double) acc.totalMembers / acc.totalEvents)
                    .avgCapacity((double) acc.totalCapacity / acc.totalEvents)
                    .build();
        };
    }

    @Override
    public Set<Characteristics> characteristics() {
        return Set.of(Characteristics.UNORDERED);
    }
}
