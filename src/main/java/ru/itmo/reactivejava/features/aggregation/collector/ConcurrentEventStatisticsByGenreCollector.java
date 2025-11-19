package ru.itmo.reactivejava.features.aggregation.collector;

import ru.itmo.reactivejava.domain.event.Event;
import ru.itmo.reactivejava.domain.event.MusicCompetitionGenre;
import ru.itmo.reactivejava.features.aggregation.viewmodel.EventStatistics;

import java.util.EnumMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

public final class ConcurrentEventStatisticsByGenreCollector implements Collector<Event, ConcurrentHashMap<MusicCompetitionGenre, EventStatisticsAccumulator>, EnumMap<MusicCompetitionGenre, EventStatistics>> {

    @Override
    public Supplier<ConcurrentHashMap<MusicCompetitionGenre, EventStatisticsAccumulator>> supplier() {
        return ConcurrentHashMap::new;
    }

    @Override
    public BiConsumer<ConcurrentHashMap<MusicCompetitionGenre, EventStatisticsAccumulator>, Event> accumulator() {
        return (map, e) -> map.compute(e.getDescription().genre(), (g, acc) -> {
            if (acc == null) acc = new EventStatisticsAccumulator();
            acc.update(e);
            return acc;
        });
    }

    @Override
    public BinaryOperator<ConcurrentHashMap<MusicCompetitionGenre, EventStatisticsAccumulator>> combiner() {
        return (left, right) -> {
            for (Map.Entry<MusicCompetitionGenre, EventStatisticsAccumulator> entry : right.entrySet()) {
                left.merge(entry.getKey(), entry.getValue(), (a, b) -> {
                    a.merge(b);
                    return a;
                });
            }
            return left;
        };
    }

    @Override
    public Function<ConcurrentHashMap<MusicCompetitionGenre, EventStatisticsAccumulator>, EnumMap<MusicCompetitionGenre, EventStatistics>> finisher() {
        return map -> {
            EnumMap<MusicCompetitionGenre, EventStatistics> res = new EnumMap<>(MusicCompetitionGenre.class);
            for (var e : map.entrySet()) {
                res.put(e.getKey(), e.getValue().toEventStatistics());
            }
            return res;
        };
    }

    @Override
    public Set<Characteristics> characteristics() {
        return Set.of(Characteristics.CONCURRENT, Characteristics.UNORDERED);
    }
}
