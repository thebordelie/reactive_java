package ru.itmo.reactivejava.features.aggregation.collector;

import ru.itmo.reactivejava.domain.event.Event;
import ru.itmo.reactivejava.features.aggregation.internal.EventStatisticsAccumulator;
import ru.itmo.reactivejava.features.aggregation.viewmodel.EventStatistics;

import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

public final class ConcurrentEventStatisticsCollector implements Collector<Event, EventStatisticsAccumulator, EventStatistics> {


    @Override
    public Supplier<EventStatisticsAccumulator> supplier() {
        return EventStatisticsAccumulator::new;
    }

    @Override
    public BiConsumer<EventStatisticsAccumulator, Event> accumulator() {
        return (acc, e) -> {
            synchronized (acc) {
                acc.update(e);
            }
        };
    }

    @Override
    public BinaryOperator<EventStatisticsAccumulator> combiner() {
        return (left, right) -> {
            left.merge(right);
            return left;
        };
    }

    @Override
    public Function<EventStatisticsAccumulator, EventStatistics> finisher() {
        return acc -> {
            if (acc.getTotalEvents() == 0) {
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
                    .totalMembers(acc.getTotalMembers())
                    .totalEvents(acc.getTotalEvents())
                    .minCapacity(acc.getMinCapacity())
                    .maxCapacity(acc.getMaxCapacity())
                    .avgMembersPerEvent((double) acc.getTotalMembers() / acc.getTotalEvents())
                    .avgCapacity((double) acc.getCapacity() / acc.getTotalEvents())
                    .build();
        };
    }

    @Override
    public Set<Characteristics> characteristics() {
        return Set.of(Characteristics.CONCURRENT, Characteristics.UNORDERED);
    }
}
