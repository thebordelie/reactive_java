package ru.itmo.reactivejava.features.aggregation.service;

import io.reactivex.rxjava3.schedulers.Schedulers;
import ru.itmo.reactivejava.domain.event.Event;
import ru.itmo.reactivejava.domain.event.MusicCompetitionGenre;
import ru.itmo.reactivejava.features.aggregation.collector.EventStatisticsAccumulator;
import ru.itmo.reactivejava.features.aggregation.viewmodel.EventStatistics;
import ru.itmo.reactivejava.features.pool.Pools;
import ru.itmo.reactivejava.features.pool.SimplePool;

import java.util.EnumMap;
import java.util.Map;

public class FlowableEventAggregationService implements EventAggregationService {

    private final SimplePool<Event> events = Pools.get(Event.class);
    private static final int BATCH_SIZE = 256;

    @Override
    public Map<MusicCompetitionGenre, EventStatistics> getStatisticsByGenre() {
        return events.flowable()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation(), false, BATCH_SIZE)
                .collect(
                        () -> new EnumMap<MusicCompetitionGenre, EventStatisticsAccumulator>(MusicCompetitionGenre.class),
                        (accs, event) -> {
                            accs.computeIfAbsent(event.getDescription().genre(),
                                            k -> new EventStatisticsAccumulator())
                                    .update(event);
                        }
                )
                .map(accumulators -> {
                    EnumMap<MusicCompetitionGenre, EventStatistics> result =
                            new EnumMap<>(MusicCompetitionGenre.class);
                    accumulators.forEach((genre, acc) -> result.put(genre, acc.toEventStatistics()));
                    return result;
                })
                .blockingGet();
    }

    @Override
    public String toString() {
        return "RxJava Flowable (backpressure)";
    }
}
