package ru.itmo.reactivejava.pool;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;

public final class SimplePool<T> implements Pool<T> {
    private final List<T> values = new ArrayList<>();

    public void add(T t) {
        values.add(t);
    }

    public void addAll(Collection<T> list) {
        values.addAll(list);
    }

    public T getRandom() {
        if (values.isEmpty()) throw new NoSuchElementException("Pool is empty");
        int i = ThreadLocalRandom.current().nextInt(values.size());
        return values.get(i);
    }

    public Collection<T> getRandom(int count) {
        Collections.shuffle(values);
        return values.subList(0, count);
    }

    public Stream<T> stream() {
        return values.stream();
    }

    @Override
    public Iterator<T> iterator() {
        return values.iterator();
    }

}
