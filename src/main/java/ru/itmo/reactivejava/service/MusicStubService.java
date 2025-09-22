package ru.itmo.reactivejava.service;

import ru.itmo.reactivejava.generator.*;
import ru.itmo.reactivejava.model.Description;
import ru.itmo.reactivejava.model.Event;
import ru.itmo.reactivejava.model.Placement;
import ru.itmo.reactivejava.model.User;
import ru.itmo.reactivejava.pool.Pools;
import ru.itmo.reactivejava.pool.SimplePool;

import java.util.List;

public class MusicStubService {

    public void printStat() {
        SimplePool<User> userSimplePool = Pools.get(User.class);
        SimplePool<Description> descriptionSimplePool = Pools.get(Description.class);
        SimplePool<Placement> placementSimplePool = Pools.get(Placement.class);
        SimplePool<Event> eventSimplePool = Pools.get(Event.class);

        UserGenerator userGenerator = new UserGenerator();
        MemberGenerator memberGenerator = new MemberGenerator();
        DescriptionGenerator descriptionGenerator = new DescriptionGenerator();
        PlacementGenerator placementGenerator = new PlacementGenerator();
        EventGenerator eventGenerator = new EventGenerator();
        List<Integer> counts = List.of(5000, 50000, 250000);
        List<AggregationService<EventStatistics>> aggregationServiceList = List.of(
                new IterativeAggregationService(),
                new StreamAggregationService(),
                new DefaultStreamAggregationService()
        );

        userSimplePool.addAll(userGenerator.generate(1000));
        descriptionSimplePool.addAll(descriptionGenerator.generate(100));
        placementSimplePool.addAll(placementGenerator.generate(50));


        for (Integer count : counts) {
            System.out.println("Прогон для " + count + " объектов\n");
            eventSimplePool.addAll(eventGenerator.generate(count));

            for (AggregationService<EventStatistics> aggregationService : aggregationServiceList) {
                long start = System.currentTimeMillis();
                System.out.println(aggregationService.toString());
                System.out.println(aggregationService.getStatistics());
                long end = System.currentTimeMillis();
                System.out.println("Затраченное время: " + (end - start) + " ms\n");
            }

            eventSimplePool.clear();
            System.out.println("-".repeat(100));
        }

    }
}
