package ru.itmo.reactivejava.model;

public record Description(
        MusicCompetitionGenre genre,
        MusicCompetitionFormat format,
        ParticipantsLevel participantsLevel,
        String description
) {
}
