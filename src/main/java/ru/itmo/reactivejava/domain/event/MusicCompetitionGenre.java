package ru.itmo.reactivejava.domain.event;

public enum MusicCompetitionGenre {
    ROCK_BATTLE("Рок"),
    HIP_HOP_BATTLE("Хип хоп"),
    JAZZ_FESTIVAL("Джаз"),
    CLASSICAL_COMPETITION("Классика");

    private final String displayName;

    MusicCompetitionGenre(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}