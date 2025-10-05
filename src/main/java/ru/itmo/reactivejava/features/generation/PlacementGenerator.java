package ru.itmo.reactivejava.features.generation;

import ru.itmo.reactivejava.domain.placement.Placement;

import java.util.*;

public class PlacementGenerator implements Generator<Placement> {

    private final HashMap<String, Integer> okatos = new HashMap<>() {{
        put("Москва", 46);
        put("Санкт-Петербург", 40);
        put("Екатеринбург", 65);
        put("Новосибирск", 50);
        put("Казань", 92);
    }};
    private final Random random = new Random();

    @Override
    public Placement generate() {
        List<Place> placesInCity = ALL_CITIES.get(random.nextInt(ALL_CITIES.size()));
        Place place = placesInCity.get(random.nextInt(placesInCity.size()));
        int capacity = generateCapacity();
        return new Placement(okatos.get(place.city), capacity, place.placement, place.address);
    }

    @Override
    public Collection<Placement> generate(int count) {
        ArrayList<Placement> placements = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            placements.add(generate());
        }
        return placements;
    }

    private int generateCapacity() {
        return random.nextInt(100, 10000);
    }

    private record Place(String city, String placement, String address) {
    }

    private static final List<Place> MOSCOW_VENUES = List.of(
            new Place("Москва", "Концертный зал Чайковского", "ул. Тверская, 31"),
            new Place("Москва", "Крокус Сити Холл", "Московская обл., г. Красногорск, 65-66 км МКАД"),
            new Place("Москва", "Зарядье", "ул. Варварка, 6"),
            new Place("Москва", "Московская консерватория", "ул. Большая Никитская, 13"),
            new Place("Москва", "Клуб RED", "ул. Сретенка, 11"),
            new Place("Москва", "Театр Эстрады", "ул. Берсеневская наб., 20")
    );

    private static final List<Place> SAINT_PETERSBURG_VENUES = List.of(
            new Place("Санкт-Петербург", "Мариинский театр", "Театральная пл., 1"),
            new Place("Санкт-Петербург", "Ледовый дворец", "пр. Пятилеток, 1"),
            new Place("Санкт-Петербург", "Филармония джазовой музыки", "ул. Загородный пр., 27"),
            new Place("Санкт-Петербург", "Клуб А2", "пр. Медиков, 3"),
            new Place("Санкт-Петербург", "БКЗ Октябрьский", "Лиговский пр., 6")
    );

    private static final List<Place> EKATERINBURG_VENUES = List.of(
            new Place("Екатеринбург", "Театр оперы и балета", "пр. Ленина, 46А"),
            new Place("Екатеринбург", "Клуб Tele-Club", "ул. Народной Воли, 65"),
            new Place("Екатеринбург", "ДИВС", "ул. Олимпийская наб., 3"),
            new Place("Екатеринбург", "Филармония", "ул. Карла Либкнехта, 38А")
    );

    private static final List<Place> NOVOSIBIRSK_VENUES = List.of(
            new Place("Новосибирск", "Театр оперы и балета", "Красный пр., 36"),
            new Place("Новосибирск", "Клуб Rock City", "ул. Каинская, 4"),
            new Place("Новосибирск", "ДК Железнодорожников", "ул. Челюскинцев, 11"),
            new Place("Новосибирск", "Филармония", "Красный пр., 32")
    );

    private static final List<Place> KAZAN_VENUES = List.of(
            new Place("Казань", "Татарский театр оперы и балета", "пл. Свободы, 2"),
            new Place("Казань", "Дворец земледельцев", "ул. Федосеевская, 36"),
            new Place("Казань", "Клуб База", "ул. Право-Булачная, 13"),
            new Place("Казань", "Центр современной культуры Смена", "ул. Бурхана Шахиди, 7")
    );

    private static final List<List<Place>> ALL_CITIES = List.of(
            MOSCOW_VENUES, SAINT_PETERSBURG_VENUES, EKATERINBURG_VENUES, NOVOSIBIRSK_VENUES, KAZAN_VENUES
    );

}
