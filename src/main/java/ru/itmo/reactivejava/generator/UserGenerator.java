package ru.itmo.reactivejava.generator;

import ru.itmo.reactivejava.model.User;

import java.util.HashMap;

// TODO Петя
public class UserGenerator extends AbstractGenerator<User> {

    @Override
    public User generateObject() {
        return new User(1);
    }
}
