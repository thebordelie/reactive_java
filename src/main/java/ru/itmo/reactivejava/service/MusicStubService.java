package ru.itmo.reactivejava.service;

import ru.itmo.reactivejava.generator.*;
import ru.itmo.reactivejava.model.*;
import ru.itmo.reactivejava.pool.Pools;
import ru.itmo.reactivejava.pool.SimplePool;

import java.util.List;

public class MusicStubService {

    public void printStat() {
        SimplePool<User> userSimplePool = Pools.get(User.class);
        SimplePool<Member> memberSimplePool = Pools.get(Member.class);
        SimplePool<Description> descriptionSimplePool = Pools.get(Description.class);
        SimplePool<Placement> placementSimplePool = Pools.get(Placement.class);
        SimplePool<Event> eventSimplePool = Pools.get(Event.class);

        UserGenerator userGenerator = new UserGenerator();
        MemberGenerator memberGenerator = new MemberGenerator();
        DescriptionGenerator descriptionGenerator = new DescriptionGenerator();
        PlacementGenerator placementGenerator = new PlacementGenerator();
        EventGenerator eventGenerator = new EventGenerator();
        List<Integer> counts = List.of(5000, 50000, 250000);
        List<AggregationService<EventStatistics, Event>> aggregationServiceList = List.of(new IterativeAggregationService(), new StreamAggregationService());

        for (Integer count : counts) {
            System.out.println("Прогон для " + count + " объектов");
            userSimplePool.addAll(userGenerator.generate(count / 100));
            memberSimplePool.addAll(memberGenerator.generate(count / 100));
            descriptionSimplePool.addAll(descriptionGenerator.generate(count / 100));
            placementSimplePool.addAll(placementGenerator.generate(count / 100));
            eventSimplePool.addAll(eventGenerator.generate(count));

            for (AggregationService<EventStatistics, Event> aggregationService : aggregationServiceList) {
                long start = System.currentTimeMillis();
                System.out.println(aggregationService.toString());
                System.out.println(aggregationService.getStatistics(eventSimplePool));
                long end = System.currentTimeMillis();
                System.out.println("Затраченное время: " + (end - start) + " ms\n");
            }

            userSimplePool.clear();
            memberSimplePool.clear();
            descriptionSimplePool.clear();
            placementSimplePool.clear();
            eventSimplePool.clear();
        }

    }
}
