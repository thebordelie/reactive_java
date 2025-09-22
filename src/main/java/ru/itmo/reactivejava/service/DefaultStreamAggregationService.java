package ru.itmo.reactivejava.service;

import ru.itmo.reactivejava.model.Event;
import ru.itmo.reactivejava.pool.Pools;
import ru.itmo.reactivejava.pool.SimplePool;
import ru.itmo.reactivejava.utils.collector.EventStatisticsCollector.EventStatisticsAccumulator;

public class DefaultStreamAggregationService implements AggregationService<EventStatistics> {

    SimplePool<Event> events = Pools.get(Event.class);

    @Override
    public EventStatistics getStatistics() {

        EventStatisticsAccumulator resultAcc = events.stream()
                .reduce(
                        new EventStatisticsAccumulator(0, 0, 0, Integer.MAX_VALUE, Integer.MIN_VALUE),
                        (acc, event) -> {
                            int capacity = event.getPlacement().getCapacity();
                            acc.totalEvents += 1;
                            acc.totalMembers += event.getMembers().size();
                            acc.totalCapacity += capacity;
                            acc.minCapacity = Math.min(acc.minCapacity, capacity);
                            acc.maxCapacity = Math.max(acc.maxCapacity, capacity);
                            return acc;
                        },
                        (acc1, acc2) -> {
                            EventStatisticsAccumulator acc = new EventStatisticsAccumulator();
                            acc.totalEvents = acc1.totalEvents + acc2.totalEvents;
                            acc.totalMembers = acc1.totalMembers + acc2.totalMembers;
                            acc.totalCapacity = acc1.totalCapacity + acc2.totalCapacity;
                            acc.minCapacity = Math.min(acc1.minCapacity, acc2.minCapacity);
                            acc.maxCapacity = Math.max(acc1.maxCapacity, acc2.maxCapacity);
                            return acc;
                        }
                );

        return EventStatistics
                .builder()
                .totalMembers(resultAcc.totalMembers)
                .totalEvents(resultAcc.totalEvents)
                .avgMembersPerEvent((double) resultAcc.totalMembers / resultAcc.totalEvents)
                .minCapacity(resultAcc.minCapacity)
                .maxCapacity(resultAcc.maxCapacity)
                .avgCapacity((double) resultAcc.totalCapacity / resultAcc.totalEvents)
                .build();
    }

    @Override
    public String toString() {
        return "Агрегация через стрим и дефолтный коллектор";
    }
}
