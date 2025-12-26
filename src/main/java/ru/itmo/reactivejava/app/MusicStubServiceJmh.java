package ru.itmo.reactivejava.app;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import ru.itmo.reactivejava.domain.event.Event;
import ru.itmo.reactivejava.domain.event.MusicCompetitionGenre;
import ru.itmo.reactivejava.domain.placement.Placement;
import ru.itmo.reactivejava.features.aggregation.service.*;
import ru.itmo.reactivejava.features.aggregation.viewmodel.EventStatistics;
import ru.itmo.reactivejava.features.generation.EventGenerator;
import ru.itmo.reactivejava.features.generation.PlacementGenerator;
import ru.itmo.reactivejava.features.generation.UserGenerator;
import ru.itmo.reactivejava.features.pool.Pools;
import ru.itmo.reactivejava.features.pool.SimplePool;
import ru.itmo.reactivejava.shared.config.DelayConfig;
import ru.itmo.reactivejava.shared.user.User;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
@Fork(1)
@Warmup(iterations = 0, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 1, time = 1, timeUnit = TimeUnit.SECONDS)
public class MusicStubServiceJmh {

    private record PoolCounts() {
        static int userCount = 1000;
        static int placementCount = 50;
    }

    @Param({
            "iterative",
            "custom-stream",
            "default-stream",
            "parallel-stream",
            "custom-parallel-stream",
            "rxjava-observable",
            "rxjava-flowable"})
    String serviceName;


    @Param({"500", "2000"})
    int eventCount;

    @Param({"true", "false"})
    boolean delayEnabled;

    private EventAggregationService aggregationService;

    @Setup(Level.Trial)
    public void trialSetup() {
        SimplePool<User> userSimplePool = Pools.get(User.class);
        SimplePool<Placement> placementSimplePool = Pools.get(Placement.class);

        UserGenerator userGenerator = new UserGenerator();
        PlacementGenerator placementGenerator = new PlacementGenerator();

        if (userSimplePool.size() == 0) {
            userSimplePool.addAll(userGenerator.generate(PoolCounts.userCount));
        }
        if (placementSimplePool.size() == 0) {
            placementSimplePool.addAll(placementGenerator.generate(PoolCounts.placementCount));
        }
    }

    @Setup(Level.Iteration)
    public void iterationSetup() throws Exception {
        DelayConfig.setEnabled(delayEnabled);
        SimplePool<Event> eventSimplePool = Pools.get(Event.class);
        EventGenerator eventGenerator = new EventGenerator();
        eventSimplePool.clear();
        eventSimplePool.addAll(eventGenerator.generate(delayEnabled ? eventCount : eventCount * 100));
        aggregationService = switch (serviceName) {
            case "iterative" -> new IterativeEventAggregationService();
            case "default-stream" -> new DefaultStreamEventAggregationService();
            case "custom-stream" -> new CustomStreamEventAggregationService();
            case "parallel-stream" -> new ParallelStreamEventAggregationService();
            case "custom-parallel-stream" -> new CustomParallelStreamEventAggregationService();
            case "rxjava-observable" -> new RxJavaEventAggregationService();
            case "rxjava-flowable" -> new FlowableEventAggregationService();
            default -> throw new Exception("Unknown service name: " + serviceName);
        };
    }

    @Benchmark
    public void aggregate(Blackhole bh) {
        Map<MusicCompetitionGenre, EventStatistics> stats = aggregationService.getStatisticsByGenre();
        bh.consume(stats);
    }
}