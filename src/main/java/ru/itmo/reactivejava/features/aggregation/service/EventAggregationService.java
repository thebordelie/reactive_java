package ru.itmo.reactivejava.features.aggregation.service;

import ru.itmo.reactivejava.domain.event.MusicCompetitionGenre;
import ru.itmo.reactivejava.features.aggregation.viewmodel.EventStatistics;

import java.util.Map;

public interface EventAggregationService extends AggregationService<EventStatistics> {
    Map<MusicCompetitionGenre, EventStatistics> getStatisticsByGenre();
}
