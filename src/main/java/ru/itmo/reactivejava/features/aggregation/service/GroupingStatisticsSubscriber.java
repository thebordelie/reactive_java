package ru.itmo.reactivejava.features.aggregation.service;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import ru.itmo.reactivejava.domain.event.Event;
import ru.itmo.reactivejava.domain.event.MusicCompetitionGenre;
import ru.itmo.reactivejava.features.aggregation.collector.ConcurrentEventStatisticsByGenreCollector;
import ru.itmo.reactivejava.features.aggregation.collector.EventStatisticsAccumulator;
import ru.itmo.reactivejava.features.aggregation.viewmodel.EventStatistics;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class GroupingStatisticsSubscriber
        implements Subscriber<Event> {

    private final ConcurrentEventStatisticsByGenreCollector collector =
            new ConcurrentEventStatisticsByGenreCollector();

    private final CompletableFuture<
            Map<MusicCompetitionGenre, EventStatistics>> resultFuture;

    private ConcurrentHashMap<
            MusicCompetitionGenre, EventStatisticsAccumulator> container;

    private Subscription subscription;
    private static final int BATCH_SIZE = 10;
    private int processed;

    public GroupingStatisticsSubscriber(
            CompletableFuture<Map<MusicCompetitionGenre, EventStatistics>> future) {
        this.resultFuture = future;
    }

    @Override
    public void onSubscribe(Subscription s) {
        this.subscription = s;
        this.container = collector.supplier().get();
        s.request(BATCH_SIZE);
    }

    @Override
    public void onNext(Event event) {
        collector.accumulator().accept(container, event);

        processed++;
        if (processed % BATCH_SIZE == 0) {
            subscription.request(BATCH_SIZE);
        }
    }

    @Override
    public void onComplete() {
        Map<MusicCompetitionGenre, EventStatistics> result =
                collector.finisher().apply(container);
        resultFuture.complete(result);
    }

    @Override
    public void onError(Throwable t) {
        resultFuture.completeExceptionally(t);
    }
}

