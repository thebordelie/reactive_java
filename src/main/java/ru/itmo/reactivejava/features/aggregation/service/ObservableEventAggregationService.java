package ru.itmo.reactivejava.features.aggregation.service;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import ru.itmo.reactivejava.domain.event.Event;
import ru.itmo.reactivejava.domain.event.MusicCompetitionGenre;
import ru.itmo.reactivejava.features.aggregation.collector.ConcurrentEventStatisticsByGenreCollector;
import ru.itmo.reactivejava.features.aggregation.viewmodel.EventStatistics;
import ru.itmo.reactivejava.features.pool.Pools;
import ru.itmo.reactivejava.features.pool.SimplePool;

import java.util.Map;

public class ObservableEventAggregationService implements EventAggregationService {

    @Override
    public Map<MusicCompetitionGenre, EventStatistics> getStatisticsByGenre() {
        SimplePool<Event> events = Pools.get(Event.class);
        return Observable.fromIterable(events)
                .groupBy(event -> event.getDescription().genre())
                .flatMap(group ->
                        group.observeOn(Schedulers.computation())
                                .collect(new ConcurrentEventStatisticsByGenreCollector())
                                .map(stats ->
                                        Map.entry(group.getKey(), stats.get(group.getKey()))
                                )
                                .toObservable())
                .toMap(Map.Entry::getKey, Map.Entry::getValue)
                .blockingGet();
    }


    @Override
    public String toString() {
        return "Rx стрим";
    }
}
