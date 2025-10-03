package ru.itmo.reactivejava.features.generation;

import ru.itmo.reactivejava.domain.event.Description;
import ru.itmo.reactivejava.domain.event.MusicCompetitionFormat;
import ru.itmo.reactivejava.domain.event.MusicCompetitionGenre;
import ru.itmo.reactivejava.domain.event.ParticipantsLevel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

public class DescriptionGenerator implements Generator<Description> {
    private final MusicCompetitionGenre[] genres = MusicCompetitionGenre.values();
    private final ParticipantsLevel[] levels = ParticipantsLevel.values();
    private final MusicCompetitionFormat[] formats = MusicCompetitionFormat.values();
    private final Random random = new Random();

    @Override
    public Description generate() {
        MusicCompetitionGenre genre = generateMusicCompetitionGenre();
        MusicCompetitionFormat competitionFormat = generateMusicCompetitionFormat();
        ParticipantsLevel participantsLevel = generateParticipantsLevel();
        String description = generateDescription(genre, participantsLevel, competitionFormat);
        return new Description(genre, competitionFormat, participantsLevel, description);
    }

    @Override
    public Collection<Description> generate(int count) {
        ArrayList<Description> descriptions = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            descriptions.add(generate());
        }
        return descriptions;
    }

    private MusicCompetitionGenre generateMusicCompetitionGenre() {
        return genres[random.nextInt(genres.length)];
    }

    private ParticipantsLevel generateParticipantsLevel() {
        return levels[random.nextInt(levels.length)];
    }

    private MusicCompetitionFormat generateMusicCompetitionFormat() {
        return formats[random.nextInt(formats.length)];
    }

    private static String generateDescription(MusicCompetitionGenre genre,
                                              ParticipantsLevel level,
                                              MusicCompetitionFormat format) {
        return String.format("%s %s в формате %s. %s",
                getLevelDescription(level),
                getGenreDescription(genre),
                getFormatDescription(format),
                getAdditionalDetails(genre, level, format)
        );
    }

    private static String getLevelDescription(ParticipantsLevel level) {
        return switch (level) {
            case PROFESSIONAL_COMPETITION -> "Профессиональное соревнование";
            case STUDENT_CHALLENGE -> "Студенческий конкурс";
            case YOUTH_FESTIVAL -> "Молодежный фестиваль";
            case INTERNATIONAL_CONTEST -> "Международный конкурс";
            case REGIONAL_CHAMPIONSHIP -> "Региональное первенство";
        };
    }

    private static String getGenreDescription(MusicCompetitionGenre genre) {
        return switch (genre) {
            case ROCK_BATTLE -> "рок-музыки";
            case HIP_HOP_BATTLE -> "хип-хоп исполнителей";
            case JAZZ_FESTIVAL -> "джазовой музыки";
            case CLASSICAL_COMPETITION -> "классической музыки";
        };
    }

    private static String getFormatDescription(MusicCompetitionFormat format) {
        return switch (format) {
            case LIVE_PERFORMANCE -> "живого выступления";
            case ONLINE_SUBMISSION -> "онлайн-заявок";
            case BLIND_AUDITION -> "слепого прослушивания";
            case TOURNAMENT_STYLE -> "турнирной системы";
            case LEAGUE_FORMAT -> "лигового формата";
        };
    }

    private static String getAdditionalDetails(MusicCompetitionGenre genre,
                                               ParticipantsLevel level,
                                               MusicCompetitionFormat format) {
        String prize = generatePrizeInfo(level);
        String requirements = generateRequirements(level);

        return String.format("%s %s", prize, requirements);
    }

    private static String generatePrizeInfo(ParticipantsLevel level) {
        return switch (level) {
            case PROFESSIONAL_COMPETITION -> "Призовой фонд: 500,000 руб. Гран-при: контракт с лейблом.";
            case INTERNATIONAL_CONTEST -> "Международное жюри. Главный приз: гастрольный тур.";
            case REGIONAL_CHAMPIONSHIP -> "Призы от спонсоров региона. Возможность участия в национальном финале.";
            case STUDENT_CHALLENGE -> "Стипендии для победителей. Стажировки в музыкальных учреждениях.";
            case YOUTH_FESTIVAL -> "Специальные призы для молодых талантов. Мастер-классы от профессионалов.";
        };
    }

    private static String generateRequirements(ParticipantsLevel level) {
        return switch (level) {
            case PROFESSIONAL_COMPETITION -> "Требуется профессиональное портфолио.";
            case INTERNATIONAL_CONTEST -> "Открыто для участников из всех стран.";
            case REGIONAL_CHAMPIONSHIP -> "Участие только для резидентов региона.";
            case STUDENT_CHALLENGE -> "Требуется подтверждение статуса студента.";
            case YOUTH_FESTIVAL -> "Возрастные ограничения: 14-25 лет.";
        };
    }
}
