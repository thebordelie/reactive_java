package ru.itmo.reactivejava.generator;

import com.github.javafaker.Faker;
import ru.itmo.reactivejava.model.Placement;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

// TODO Артем
public class PlacementGenerator implements Generator<Placement> {

    Faker faker = new Faker();

    @Override
    public Placement generate() {
        return new Placement(
                generateOkato(),
                generateAddress(),
                generateCapacity()
        );
    }

    @Override
    public Collection<Placement> generate(int count) {
        ArrayList<Placement> placements = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            placements.add(generate());
        }
        return placements;
    }

    public String generateOkato() {
        return "12345678901";
    }

    public String generateAddress() {
        return faker.address().fullAddress();
    }

    public int generateCapacity() {
        return new Random().nextInt(0, 1000000);
    }
}
