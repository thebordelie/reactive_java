package ru.itmo.reactivejava.service;

import ru.itmo.reactivejava.pool.SimplePool;

public interface AggregationService<T, V> {
    T getStatistics(SimplePool<V> objects);
}
