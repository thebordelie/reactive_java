package ru.itmo.reactivejava.generator;

import java.util.HashMap;

abstract public class AbstractGenerator<E> implements Generator<E> {

    protected HashMap<Class, Object> properties;

    public AbstractGenerator() {
    }


    public AbstractGenerator(HashMap<Class, Object> properties) {
        this.properties = properties;
    }


}
