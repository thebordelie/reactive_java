package ru.itmo.reactivejava.pool;

import lombok.NoArgsConstructor;

import java.util.concurrent.ConcurrentHashMap;

@NoArgsConstructor
public final class Pools {
    private final static ConcurrentHashMap<Class<?>, SimplePool<?>> REGISTRY = new ConcurrentHashMap<>();

    public static <T> SimplePool<T> get(Class<T> clazz) {
        //noinspection unchecked
        return (SimplePool<T>) REGISTRY.computeIfAbsent(clazz, k -> new SimplePool<T>());
    }
}
