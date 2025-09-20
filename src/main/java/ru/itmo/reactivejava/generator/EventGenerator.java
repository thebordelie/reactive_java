package ru.itmo.reactivejava.generator;

import ru.itmo.reactivejava.model.Event;

import java.util.ArrayList;
import java.util.Collection;

// TODO Артем
public class EventGenerator implements Generator<Event> {

    @Override
    public Event generate() {
        return new Event();
    }

    @Override
    public Collection<Event> generate(int count) {


        ArrayList<Event> events = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Event event = generate();
            MemberGenerator memberGenerator = new MemberGenerator(event);
            event.addMembers(new ArrayList<>(memberGenerator.generate(500)));
            events.add(event);
        }
        return events;
    }
}
