package ru.itmo.reactivejava.features.aggregation.service;

import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import ru.itmo.reactivejava.domain.event.Event;
import ru.itmo.reactivejava.domain.event.MusicCompetitionGenre;
import ru.itmo.reactivejava.features.aggregation.viewmodel.EventStatistics;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class FlowableEventAggregationService implements EventAggregationService {
    @Override
    public Map<MusicCompetitionGenre, EventStatistics> getStatisticsByGenre() {
        Flowable<Event> flowable = Flowable.create(null, BackpressureStrategy.BUFFER);
        CompletableFuture<
                Map<MusicCompetitionGenre, EventStatistics>> future =
                new CompletableFuture<>();

        flowable
                .groupBy(event -> event.getDescription().genre())
                .flatMap(group ->
                        group.parallel()
                                .runOn(Schedulers.computation())
                                .sequential()
                )
                .subscribe(new GroupingStatisticsSubscriber(future));
        return Map.of();
    }
}
