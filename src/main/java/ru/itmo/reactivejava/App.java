package ru.itmo.reactivejava;

import ru.itmo.reactivejava.service.MusicStubService;

public class App {
    public static void main(String[] args) {
        MusicStubService musicStubService = new MusicStubService();
        musicStubService.printStat();
    }
}
