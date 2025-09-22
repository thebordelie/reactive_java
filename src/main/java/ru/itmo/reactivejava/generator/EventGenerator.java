package ru.itmo.reactivejava.generator;

import ru.itmo.reactivejava.model.Description;
import ru.itmo.reactivejava.model.Event;
import ru.itmo.reactivejava.model.Member;
import ru.itmo.reactivejava.model.Placement;
import ru.itmo.reactivejava.pool.Pools;
import ru.itmo.reactivejava.pool.SimplePool;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

public class EventGenerator implements Generator<Event> {
    private long currentId = 1;
    private final Random random = new Random();
    private final SimplePool<Placement> placementsPool = Pools.get(Placement.class);
    private final MemberGenerator memberGenerator = new MemberGenerator();
    private final DescriptionGenerator descriptionGenerator = new DescriptionGenerator();

    @Override
    public Event generate() {
        long id = generateId();
        String name = generateName();
        LocalDateTime time = generateRandomDateTime();
        Description description = generateRandomDescription();
        Placement placement = generateRandomPlacement();
        List<Member> members = new ArrayList<>(generateRandomMembers());
        return new Event(id, name, time, description, placement, members);
    }

    @Override
    public Collection<Event> generate(int count) {
        ArrayList<Event> events = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            events.add(generate());
        }
        return events;
    }

    private Collection<Member> generateRandomMembers() {
        return memberGenerator.generate(random.nextInt(0, 100));
    }

    private Description generateRandomDescription() {
        return descriptionGenerator.generate();
    }

    private String generateName() {
        return String.format("Музыкальное событие №%d", currentId);
    }

    private Placement generateRandomPlacement() {
        return placementsPool.getRandom();
    }

    private long generateId() {
        return currentId++;
    }

    private LocalDateTime generateRandomDateTime() {
        LocalDate date = generateDate();
        LocalTime time = generateTime();
        return LocalDateTime.of(date, time);
    }

    private LocalDate generateDate() {
        LocalDate start = LocalDate.now().plusDays(random.nextInt(10));
        LocalDate end = LocalDate.now().plusYears(1);
        long randomDate = random.nextLong(start.toEpochDay(), end.toEpochDay());
        return LocalDate.ofEpochDay(randomDate);
    }

    public LocalTime generateTime() {
        int hour = random.nextInt(0, 24);
        int minute = random.nextInt(0, 60);
        return LocalTime.of(hour, minute);
    }
}
