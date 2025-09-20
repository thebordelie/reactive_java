package ru.itmo.reactivejava.generator;


import java.util.Collection;

public interface Generator<E> {
    E generate();

    Collection<E> generate(int count);
}
