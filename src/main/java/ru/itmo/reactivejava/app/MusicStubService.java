package ru.itmo.reactivejava.app;

import de.vandermeer.asciitable.AsciiTable;
import ru.itmo.reactivejava.domain.event.Event;
import ru.itmo.reactivejava.domain.placement.Placement;
import ru.itmo.reactivejava.features.aggregation.service.DefaultEventStreamAggregationService;
import ru.itmo.reactivejava.features.aggregation.service.EventAggregationService;
import ru.itmo.reactivejava.features.aggregation.service.IterativeEventAggregationService;
import ru.itmo.reactivejava.features.aggregation.service.StreamEventAggregationService;
import ru.itmo.reactivejava.features.generation.EventGenerator;
import ru.itmo.reactivejava.features.generation.PlacementGenerator;
import ru.itmo.reactivejava.features.generation.UserGenerator;
import ru.itmo.reactivejava.features.pool.Pools;
import ru.itmo.reactivejava.features.pool.SimplePool;
import ru.itmo.reactivejava.shared.user.User;

import java.util.*;

public class MusicStubService {

    private record PoolCounts() {
        static int userCount = 1000;
        static int placementCount = 50;
        static int eventCount = 250000;
    }

    public void init() {

        SimplePool<User> userSimplePool = Pools.get(User.class);
        SimplePool<Placement> placementSimplePool = Pools.get(Placement.class);

        UserGenerator userGenerator = new UserGenerator();
        PlacementGenerator placementGenerator = new PlacementGenerator();

        userSimplePool.addAll(userGenerator.generate(PoolCounts.userCount));
        placementSimplePool.addAll(placementGenerator.generate(PoolCounts.placementCount));

        printStat();
    }


    private record TimeResult(String service, int count, long timeMs) {
    }

    private record Result(EventAggregationService service, int count, long timeMs, Object stats) {
    }

    private static void printTimeTable(List<TimeResult> rows) {
        List<TimeResult> sorted = new ArrayList<>(rows);
        sorted.sort(Comparator.comparingInt(TimeResult::count).thenComparing(TimeResult::service));

        Map<Integer, List<TimeResult>> byCount = new LinkedHashMap<>();
        for (TimeResult r : sorted) {
            byCount.computeIfAbsent(r.count(), k -> new ArrayList<>()).add(r);
        }

        for (Map.Entry<Integer, List<TimeResult>> entry : byCount.entrySet()) {
            int count = entry.getKey();
            List<TimeResult> group = entry.getValue();
            System.out.println("Count: " + count);
            AsciiTable at = new AsciiTable();
            at.addRule();
            at.addRow("Service", "Time, ms");
            at.addRule();
            for (TimeResult r : group) {
                at.addRow(r.service(), r.timeMs());
                at.addRule();
            }
            System.out.println(at.render());
            System.out.println();
        }
    }

    private static void printResults(List<Result> results) {
        List<TimeResult> timeRows = new ArrayList<>();
        for (Result r : results) {
            timeRows.add(new TimeResult(r.service().toString(), r.count(), r.timeMs()));
        }
        printTimeTable(timeRows);
    }

    private void printStat() {

        SimplePool<Event> eventSimplePool = Pools.get(Event.class);
        EventGenerator eventGenerator = new EventGenerator();

        List<Integer> counts = List.of(
                PoolCounts.eventCount / 50,
                PoolCounts.eventCount / 5,
                PoolCounts.eventCount
        );
        List<EventAggregationService> aggregationServiceList = List.of(
                new IterativeEventAggregationService(),
                new StreamEventAggregationService(),
                new DefaultEventStreamAggregationService()
        );

        List<Result> results = new ArrayList<>();

        for (Integer count : counts) {
            eventSimplePool.clear();
            eventSimplePool.addAll(eventGenerator.generate(count));

            for (EventAggregationService aggregationService : aggregationServiceList) {
                long start = System.currentTimeMillis();
                var stats = aggregationService.getStatisticsByGenre();
                long end = System.currentTimeMillis();
                results.add(new Result(aggregationService, count, end - start, stats));
            }
        }

        printResults(results);

    }
}
