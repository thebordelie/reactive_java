package ru.itmo.reactivejava;

import ru.itmo.reactivejava.generator.*;
import ru.itmo.reactivejava.model.*;
import ru.itmo.reactivejava.pool.Pools;
import ru.itmo.reactivejava.pool.SimplePool;
import ru.itmo.reactivejava.service.EventStatistics;
import ru.itmo.reactivejava.service.IterativeAggregationService;
import ru.itmo.reactivejava.service.MusicStubService;
import ru.itmo.reactivejava.service.StreamAggregationService;

public class App {

    public static void main(String[] args) {

        MusicStubService musicStubService = new MusicStubService();
        musicStubService.printStat();
    }
}
