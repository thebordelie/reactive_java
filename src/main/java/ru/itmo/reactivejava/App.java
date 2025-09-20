package ru.itmo.reactivejava;

import ru.itmo.reactivejava.generator.*;
import ru.itmo.reactivejava.model.*;
import ru.itmo.reactivejava.pool.Pools;
import ru.itmo.reactivejava.pool.SimplePool;

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

        // simple generate
        userSimplePool.addAll(userGenerator.generate(10000));
        memberSimplePool.addAll(memberGenerator.generate(500));
        descriptionSimplePool.addAll(descriptionGenerator.generate(100));
        placementSimplePool.addAll(placementGenerator.generate(100));
        eventSimplePool.addAll(eventGenerator.generate(100));

        // Аггрегация через цикл
        long startCycle = System.currentTimeMillis();
        int eventIndex = 1;
        for (Event event : eventSimplePool) {
            long guestCount = 0;
            for (Member member : event.getMembers()) {
                if (member.getMemberType() == MemberType.GUEST) {
                    guestCount += 1;
                }
            }
            System.out.printf("Event %d: %d гостей через цикл%n", eventIndex++, guestCount);
        }
        System.out.printf("Аггрегация через цикл заняла %d мс%n", System.currentTimeMillis() - startCycle);


        // Аггрегация через стрим
        long startStream = System.currentTimeMillis();
        final int[] streamIndex = {1};
        eventSimplePool.stream().forEach(e -> {
            long guestCount = e.getMembers().stream()
                    .filter(m -> m.getMemberType() == MemberType.GUEST)
                    .count();
            System.out.printf("Event %d: %d гостей через стрим%n", streamIndex[0]++, guestCount);
        });
        System.out.printf("Аггрегация через стрим заняла %d мс%n", System.currentTimeMillis() - startStream);


    }
}
