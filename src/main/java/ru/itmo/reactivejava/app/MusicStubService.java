package ru.itmo.reactivejava.app;

import de.vandermeer.asciitable.AsciiTable;
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
import ru.itmo.reactivejava.shared.user.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

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

    private record Result(
            EventAggregationService service,
            long timeMs,
            Map<MusicCompetitionGenre, EventStatistics> statisticsByGenre) {
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
                new CustomStreamEventAggregationService(),
                new DefaultStreamEventAggregationService(),
                new ParallelStreamEventAggregationService(),
                new CustomParallelStreamEventAggregationService()
        );


        for (Integer count : counts) {
            List<Result> results = new ArrayList<>();

            eventSimplePool.addAll(eventGenerator.generate(count));
            for (EventAggregationService aggregationService : aggregationServiceList) {
                long start = System.currentTimeMillis();
                var stats = aggregationService.getStatisticsByGenre();
                long end = System.currentTimeMillis();
                results.add(new Result(aggregationService, end - start, stats));
            }
            eventSimplePool.clear();

            printResultsByCount(results, count);
        }
    }

    private static void printResultsByCount(List<Result> results, int count) {
        System.out.println("Count: " + count);
        printTimeTable(results);
        printStatisticsByGenreTable(results);
    }

    private static void printTimeTable(List<Result> rows) {
        AsciiTable at = new AsciiTable();
        at.addRule();
        at.addRow("Service", "Time, ms");
        for (Result row : rows) {
            at.addRule();
            at.addRow(row.service(), row.timeMs());
        }
        at.addRule();
        System.out.println(at.render());
        System.out.println();
    }

    private static void printStatisticsByGenreTable(List<Result> rows) {
        AsciiTable at = new AsciiTable();
        List<String> genres = Arrays.stream(MusicCompetitionGenre.values()).map(Enum::toString).toList();
        List<String> services = rows.stream().map(r -> r.service.toString()).toList();
        at.addRule();
        int size = genres.size() * services.size() + 1;

        int index = 0;
        String[] firstRow = new String[size];
        firstRow[index] = "";
        for (String service : services) {
            index += genres.size();
            firstRow[index] = service;
        }
        at.addRow(firstRow);
        at.addRule();

        index = 0;
        String[] lastRow = new String[size];
        lastRow[index++] = "Атрибут";
        for (String service : services) {
            for (String genre : genres) {
                lastRow[index++] = genre;
            }
        }
        at.addRow(lastRow);
        at.addRule();


        String[] metrics = {"totalEvents", "totalMembers", "avgMembers", "minCapacity", "maxCapacity", "avgCapacity"};
        for (String metric : metrics) {
            String[] row = new String[size];
            int i = 0;
            row[i++] = metric;
            for (Result r : rows) {
                for (MusicCompetitionGenre g : MusicCompetitionGenre.values()) {
                    EventStatistics st = r.statisticsByGenre().get(g);
                    row[i++] = (st == null)
                            ? (metric.startsWith("avg") ? "0.0" : "0")
                            : switch (metric) {
                        case "totalEvents" -> String.valueOf(st.getTotalEvents());
                        case "totalMembers" -> String.valueOf(st.getTotalMembers());
                        case "avgMembers" -> String.format("%.1f", st.getAvgMembersPerEvent());
                        case "minCapacity" -> String.valueOf(st.getMinCapacity());
                        case "maxCapacity" -> String.valueOf(st.getMaxCapacity());
                        default -> String.format("%.1f", st.getAvgCapacity());
                    };
                }
            }
            at.addRow((Object[]) row);
            at.addRule();
        }

        System.out.println(at.render(170));
        System.out.println();
    }

}
