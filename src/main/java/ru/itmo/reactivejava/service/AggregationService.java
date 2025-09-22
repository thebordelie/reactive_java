package ru.itmo.reactivejava.service;

import ru.itmo.reactivejava.pool.SimplePool;

public interface AggregationService<T> {
    T getStatistics();
}
