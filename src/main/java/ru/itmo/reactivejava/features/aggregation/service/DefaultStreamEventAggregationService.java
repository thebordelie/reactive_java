package ru.itmo.reactivejava.features.aggregation.service;

import ru.itmo.reactivejava.domain.event.Event;
import ru.itmo.reactivejava.domain.event.MusicCompetitionGenre;
import ru.itmo.reactivejava.features.aggregation.viewmodel.EventStatistics;
import ru.itmo.reactivejava.features.pool.Pools;
import ru.itmo.reactivejava.features.pool.SimplePool;

import java.util.EnumMap;
import java.util.Map;

import static java.util.stream.Collectors.*;

public class DefaultStreamEventAggregationService implements EventAggregationService {

    SimplePool<Event> events = Pools.get(Event.class);

    @Override
    public EventStatistics getStatistics() {
        return events.stream().collect(
                teeing(
                        summarizingInt(e -> e.getPlacement().getCapacity()),
                        summingInt(e -> e.getMembers().size()),
                        (capStats, totalMembers) -> {
                            long n = capStats.getCount();
                            double avgMembers = n > 0 ? (double) totalMembers / n : 0.0;
                            return EventStatistics.builder()
                                    .totalMembers(totalMembers)
                                    .totalEvents((int) n)
                                    .avgMembersPerEvent(avgMembers)
                                    .minCapacity(n > 0 ? capStats.getMin() : 0)
                                    .maxCapacity(n > 0 ? capStats.getMax() : 0)
                                    .avgCapacity(n > 0 ? capStats.getAverage() : 0.0)
                                    .build();
                        }
                )
        );
    }

    @Override
    public Map<MusicCompetitionGenre, EventStatistics> getStatisticsByGenre() {
        return events.stream().collect(
                groupingBy(
                        e -> e.getDescription().genre(),
                        () -> new EnumMap<>(MusicCompetitionGenre.class),
                        teeing(
                                summarizingInt(e -> e.getPlacement().getCapacity()),
                                summingInt(e -> e.getMembers().size()),
                                (capStats, totalMembers) -> {
                                    long n = capStats.getCount();
                                    double avgMembers = n > 0 ? (double) totalMembers / n : 0.0;
                                    return EventStatistics.builder()
                                            .totalMembers(totalMembers)
                                            .totalEvents((int) n)
                                            .avgMembersPerEvent(avgMembers)
                                            .minCapacity(n > 0 ? capStats.getMin() : 0)
                                            .maxCapacity(n > 0 ? capStats.getMax() : 0)
                                            .avgCapacity(n > 0 ? capStats.getAverage() : 0.0)
                                            .build();
                                }
                        )
                )
        );
    }

    @Override
    public String toString() {
        return "Дефолтный стрим";
    }
}
