package ru.itmo.reactivejava.features.generation;


import java.util.Collection;

public interface Generator<E> {
    E generate();

    Collection<E> generate(int count);
}
