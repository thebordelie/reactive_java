package ru.itmo.reactivejava;

import ru.itmo.reactivejava.generator.*;
import ru.itmo.reactivejava.model.*;
import ru.itmo.reactivejava.pool.Pools;
import ru.itmo.reactivejava.pool.SimplePool;
import ru.itmo.reactivejava.service.EventStatistics;
import ru.itmo.reactivejava.service.IterativeAggregationService;
import ru.itmo.reactivejava.service.StreamAggregationService;

public class App {

    public static void main(String[] args) {

        // Pools
        SimplePool<User> userSimplePool = Pools.get(User.class);
        SimplePool<Member> memberSimplePool = Pools.get(Member.class);
        SimplePool<Description> descriptionSimplePool = Pools.get(Description.class);
        SimplePool<Placement> placementSimplePool = Pools.get(Placement.class);
        SimplePool<Event> eventSimplePool = Pools.get(Event.class);

        // Generators
        UserGenerator userGenerator = new UserGenerator();
        MemberGenerator memberGenerator = new MemberGenerator();
        DescriptionGenerator descriptionGenerator = new DescriptionGenerator();
        PlacementGenerator placementGenerator = new PlacementGenerator();
        EventGenerator eventGenerator = new EventGenerator();

        // Simple generate
        userSimplePool.addAll(userGenerator.generate(10000));
        memberSimplePool.addAll(memberGenerator.generate(500));
        descriptionSimplePool.addAll(descriptionGenerator.generate(100));
        placementSimplePool.addAll(placementGenerator.generate(100));
        eventSimplePool.addAll(eventGenerator.generate(100));

        // Аггрегация через цикл
        IterativeAggregationService iterativeAggregationService = new IterativeAggregationService();
        EventStatistics iterativeEventStatistics = iterativeAggregationService.getStatistics(eventSimplePool);
        System.out.println(iterativeEventStatistics);

        // Аггрегация через стрим и кастомный коллектор
        StreamAggregationService streamAggregationService = new StreamAggregationService();
        EventStatistics streamEventStatistics = streamAggregationService.getStatistics(eventSimplePool);
        System.out.println(streamEventStatistics);

    }
}
