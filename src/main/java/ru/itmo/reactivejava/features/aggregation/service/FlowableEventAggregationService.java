package ru.itmo.reactivejava.features.aggregation.service;

import io.reactivex.rxjava3.schedulers.Schedulers;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import ru.itmo.reactivejava.domain.event.Event;
import ru.itmo.reactivejava.domain.event.MusicCompetitionGenre;
import ru.itmo.reactivejava.features.aggregation.collector.EventStatisticsAccumulator;
import ru.itmo.reactivejava.features.aggregation.viewmodel.EventStatistics;
import ru.itmo.reactivejava.features.pool.Pools;
import ru.itmo.reactivejava.features.pool.SimplePool;

import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

public class FlowableEventAggregationService implements EventAggregationService {

    private final SimplePool<Event> events = Pools.get(Event.class);
    private static final int BATCH_SIZE = 256;

    @Override
    public Map<MusicCompetitionGenre, EventStatistics> getStatisticsByGenre() {
        EnumMap<MusicCompetitionGenre, EventStatisticsAccumulator> accumulators =
                new EnumMap<>(MusicCompetitionGenre.class);
        CountDownLatch latch = new CountDownLatch(1);

        events.flowable()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation(), false, BATCH_SIZE)
                .subscribe(new EventStatisticsSubscriber(accumulators, latch, BATCH_SIZE));

        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        Map<MusicCompetitionGenre, EventStatistics> result = new EnumMap<>(MusicCompetitionGenre.class);
        accumulators.forEach((genre, acc) -> result.put(genre, acc.toEventStatistics()));
        return result;
    }

    private static class EventStatisticsSubscriber implements Subscriber<Event> {
        private final EnumMap<MusicCompetitionGenre, EventStatisticsAccumulator> accumulators;
        private final CountDownLatch latch;
        private final int batchSize;
        private Subscription subscription;
        private int processed = 0;

        public EventStatisticsSubscriber(
                EnumMap<MusicCompetitionGenre, EventStatisticsAccumulator> accumulators,
                CountDownLatch latch,
                int batchSize) {
            this.accumulators = accumulators;
            this.latch = latch;
            this.batchSize = batchSize;
        }

        @Override
        public void onSubscribe(Subscription s) {
            this.subscription = s;
            subscription.request(batchSize);
        }

        @Override
        public void onNext(Event event) {
            MusicCompetitionGenre genre = event.getDescription().genre();
            int capacity = event.getPlacement().getCapacity();
            int membersCount = event.getMembers().size();

            accumulators.computeIfAbsent(genre, k -> new EventStatisticsAccumulator())
                    .updateWithData(capacity, membersCount);

            processed++;
            if (processed % batchSize == 0) {
                subscription.request(batchSize);
            }
        }

        @Override
        public void onError(Throwable t) {
            t.printStackTrace();
            latch.countDown();
        }

        @Override
        public void onComplete() {
            latch.countDown();
        }
    }

    @Override
    public String toString() {
        return "RxJava Flowable (backpressure)";
    }
}
