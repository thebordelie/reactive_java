package ru.itmo.reactivejava.domain.event;

public record Description(
        MusicCompetitionGenre genre,
        MusicCompetitionFormat format,
        ParticipantsLevel participantsLevel,
        String description
) {
}
