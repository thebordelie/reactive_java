package ru.itmo.reactivejava.features.pool;

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

    public void clear() {
        values.clear();
    }

    public int size() {
        return values.size();
    }

    public Stream<T> stream() {
        return values.stream();
    }

    public Stream<T> parallelStream(){
        return values.parallelStream();
    }

    @Override
    public Iterator<T> iterator() {
        return values.iterator();
    }

}
