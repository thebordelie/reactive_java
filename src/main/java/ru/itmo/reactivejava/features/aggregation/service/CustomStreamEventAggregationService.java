package ru.itmo.reactivejava.features.aggregation.service;

import ru.itmo.reactivejava.domain.event.Event;
import ru.itmo.reactivejava.domain.event.MusicCompetitionGenre;
import ru.itmo.reactivejava.features.aggregation.collector.EventStatisticsByGenreCollector;
import ru.itmo.reactivejava.features.aggregation.viewmodel.EventStatistics;
import ru.itmo.reactivejava.features.pool.Pools;
import ru.itmo.reactivejava.features.pool.SimplePool;

import java.util.Map;

public class CustomStreamEventAggregationService implements EventAggregationService {

    SimplePool<Event> events = Pools.get(Event.class);

    @Override
    public Map<MusicCompetitionGenre, EventStatistics> getStatisticsByGenre() {
        return events.stream().collect(new EventStatisticsByGenreCollector());
    }

    @Override
    public String toString() {
        return "Кастомный стрим";
    }
}
