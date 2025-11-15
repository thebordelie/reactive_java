package ru.itmo.reactivejava.features.pool;

import ru.itmo.reactivejava.domain.event.Event;

import java.util.List;
import java.util.Spliterator;
import java.util.function.Consumer;

public class EventSpliterator implements Spliterator<Event> {
    private final List<Event> events;
    private int currentIndex;
    private final int fence;
    private static final int MIN_CHUNK_SIZE = 100;

    public EventSpliterator(List<Event> events) {
        this(events, 0, events.size());
    }

    private EventSpliterator(List<Event> events, int origin, int fence) {
        this.events = events;
        this.currentIndex = origin;
        this.fence = fence;
    }

    @Override
    public boolean tryAdvance(Consumer<? super Event> action) {
        if (currentIndex < fence) {
            action.accept(events.get(currentIndex));
            currentIndex++;
            return true;
        }
        return false;
    }

    @Override
    public Spliterator<Event> trySplit() {
        int remainingSize = fence - currentIndex;
        if (remainingSize < MIN_CHUNK_SIZE * 2) {
            return null;
        }
        int splitPos = currentIndex + remainingSize / 2;
        EventSpliterator prefix = new EventSpliterator(events, currentIndex, splitPos);
        currentIndex = splitPos;
        return prefix;
    }

    @Override
    public long estimateSize() {
        return fence - currentIndex;
    }

    @Override
    public int characteristics() {
        return SIZED | SUBSIZED | IMMUTABLE;
    }
}
