package ru.itmo.reactivejava.features.aggregation.collector;

import ru.itmo.reactivejava.domain.event.Event;
import ru.itmo.reactivejava.domain.event.MusicCompetitionGenre;
import ru.itmo.reactivejava.features.aggregation.internal.EventStatisticsAccumulator;
import ru.itmo.reactivejava.features.aggregation.viewmodel.EventStatistics;

import java.util.EnumMap;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

public final class EventStatisticsByGenreCollector implements Collector<Event, EnumMap<MusicCompetitionGenre, EventStatisticsAccumulator>, EnumMap<MusicCompetitionGenre, EventStatistics>> {

    @Override
    public Supplier<EnumMap<MusicCompetitionGenre, EventStatisticsAccumulator>> supplier() {
        return () -> new EnumMap<>(MusicCompetitionGenre.class);
    }

    @Override
    public BiConsumer<EnumMap<MusicCompetitionGenre, EventStatisticsAccumulator>, Event> accumulator() {
        return (map, e) ->
                map.computeIfAbsent(e.getDescription().genre(), g -> new EventStatisticsAccumulator()).update(e);
    }

    @Override
    public BinaryOperator<EnumMap<MusicCompetitionGenre, EventStatisticsAccumulator>> combiner() {
        return (left, right) -> {
            if (left.size() < right.size()) {
                var tmp = left;
                left = right;
                right = tmp;
            }
            for (var entry : right.entrySet()) {
                EventStatisticsAccumulator acc = left.computeIfAbsent(entry.getKey(), k -> new EventStatisticsAccumulator());
                acc.merge(entry.getValue());
            }
            return left;
        };
    }

    @Override
    public Function<EnumMap<MusicCompetitionGenre, EventStatisticsAccumulator>, EnumMap<MusicCompetitionGenre, EventStatistics>> finisher() {
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
        return Set.of(Characteristics.UNORDERED);
    }
}
