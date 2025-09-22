package ru.itmo.reactivejava.generator;

import com.github.javafaker.Faker;
import ru.itmo.reactivejava.model.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

public class UserGenerator implements Generator<User> {

    int currentId = 1;
    Faker faker = new Faker();
    Random random = new Random();

    @Override
    public User generate() {
        return new User(
                generateId(),
                generateFirstName(),
                generateLastName(),
                generateAge(),
                generateEmail()
        );
    }

    @Override
    public Collection<User> generate(int count) {
        ArrayList<User> users = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            users.add(generate());
        }
        return users;
    }

    private int generateId() {
        return currentId++;
    }

    private String generateFirstName() {
        return faker.name().firstName();
    }

    private String generateLastName() {
        return faker.name().lastName();
    }

    private int generateAge() {
        return random.nextInt(12, 100);
    }

    private String generateEmail() {
        return faker.internet().emailAddress();
    }

}
