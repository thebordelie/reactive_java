package ru.itmo.reactivejava.features.aggregation.service;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import ru.itmo.reactivejava.domain.event.Event;
import ru.itmo.reactivejava.domain.event.MusicCompetitionGenre;
import ru.itmo.reactivejava.features.aggregation.collector.EventStatisticsAccumulator;
import ru.itmo.reactivejava.features.aggregation.viewmodel.EventStatistics;
import ru.itmo.reactivejava.features.pool.Pools;
import ru.itmo.reactivejava.features.pool.SimplePool;

import java.util.EnumMap;
import java.util.Map;

public class RxJavaEventAggregationService implements EventAggregationService {

    private final SimplePool<Event> events = Pools.get(Event.class);

    private record EventData(MusicCompetitionGenre genre, int capacity, int membersCount) {}

    @Override
    public Map<MusicCompetitionGenre, EventStatistics> getStatisticsByGenre() {
        return events.observable()
                .flatMap(event -> Observable.just(event)
                        .subscribeOn(Schedulers.io())
                        .map(e -> new EventData(
                                e.getDescription().genre(),
                                e.getPlacement().getCapacity(),
                                e.getMembers().size())))
                .groupBy(EventData::genre)
                .flatMapSingle(group -> group
                        .collect(
                                EventStatisticsAccumulator::new,
                                (acc, data) -> acc.updateWithData(data.capacity(), data.membersCount()))
                        .map(acc -> Map.entry(group.getKey(), acc.toEventStatistics())))
                .collect(
                        () -> new EnumMap<MusicCompetitionGenre, EventStatistics>(MusicCompetitionGenre.class),
                        (map, entry) -> map.put(entry.getKey(), entry.getValue()))
                .blockingGet();
    }

    @Override
    public String toString() {
        return "RxJava Observable";
    }
}
