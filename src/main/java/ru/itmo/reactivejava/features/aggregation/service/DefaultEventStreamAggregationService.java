package ru.itmo.reactivejava.features.aggregation.service;

import ru.itmo.reactivejava.domain.event.Event;
import ru.itmo.reactivejava.domain.event.MusicCompetitionGenre;
import ru.itmo.reactivejava.features.aggregation.internal.EventStatisticsAccumulator;
import ru.itmo.reactivejava.features.aggregation.viewmodel.EventStatistics;
import ru.itmo.reactivejava.features.pool.Pools;
import ru.itmo.reactivejava.features.pool.SimplePool;

import java.util.EnumMap;
import java.util.IntSummaryStatistics;
import java.util.Map;

import static java.util.stream.Collectors.*;

public class DefaultEventStreamAggregationService implements EventAggregationService {

    SimplePool<Event> events = Pools.get(Event.class);

    @Override
    public EventStatistics getStatistics() {
        class R {
            IntSummaryStatistics capStats;
            Integer totalMembers;
        }

        R r = events.stream().collect(
                teeing(
                        summarizingInt(e -> e.getPlacement().getCapacity()),
                        summingInt(e -> e.getMembers().size()),
                        (capStats, totalMembers) -> {
                            R tmp = new R();
                            tmp.capStats = capStats;
                            tmp.totalMembers = totalMembers;
                            return tmp;
                        }
                )
        );

        long n = r.capStats.getCount();
        double avgMembers = n > 0 ? (double) r.totalMembers / n : 0.0;

        return EventStatistics.builder()
                .totalMembers(r.totalMembers)
                .totalEvents((int) n)
                .avgMembersPerEvent(avgMembers)
                .minCapacity(n > 0 ? r.capStats.getMin() : 0)
                .maxCapacity(n > 0 ? r.capStats.getMax() : 0)
                .avgCapacity(n > 0 ? r.capStats.getAverage() : 0.0)
                .build();
    }

    @Override
    public Map<MusicCompetitionGenre, EventStatistics> getStatisticsByGenre() {

        return events.stream().collect(
                collectingAndThen(
                        toMap(
                                e -> e.getDescription().genre(),
                                EventStatisticsAccumulator::new,
                                (a, b) -> {
                                    a.merge(b);
                                    return a;
                                },
                                () -> new EnumMap<>(MusicCompetitionGenre.class)
                        ),
                        accMap -> accMap.entrySet().stream().collect(
                                toMap(
                                        Map.Entry::getKey,
                                        e -> e.getValue().toEventStatistics(),
                                        (x, y) -> x,
                                        () -> new EnumMap<>(MusicCompetitionGenre.class)
                                )
                        )
                )
        );
    }

    @Override
    public String toString() {
        return "Дефолтный стрим";
    }
}
