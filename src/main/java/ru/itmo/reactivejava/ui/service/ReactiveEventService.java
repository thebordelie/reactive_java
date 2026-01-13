package ru.itmo.reactivejava.ui.service;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.subjects.BehaviorSubject;
import io.reactivex.rxjava3.subjects.PublishSubject;
import ru.itmo.reactivejava.domain.event.Description;
import ru.itmo.reactivejava.domain.event.Event;
import ru.itmo.reactivejava.domain.event.MusicCompetitionGenre;
import ru.itmo.reactivejava.domain.member.Member;
import ru.itmo.reactivejava.domain.member.MemberType;
import ru.itmo.reactivejava.domain.placement.Placement;
import ru.itmo.reactivejava.features.generation.EventGenerator;
import ru.itmo.reactivejava.features.generation.MemberGenerator;
import ru.itmo.reactivejava.features.generation.PlacementGenerator;
import ru.itmo.reactivejava.features.generation.UserGenerator;
import ru.itmo.reactivejava.features.pool.Pools;
import ru.itmo.reactivejava.features.pool.SimplePool;
import ru.itmo.reactivejava.shared.user.User;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

public class ReactiveEventService {

    private final BehaviorSubject<List<Event>> eventsSubject = BehaviorSubject.createDefault(new ArrayList<>());
    private final PublishSubject<Notification> notificationSubject = PublishSubject.create();
    private final PublishSubject<MemberUpdate> memberUpdateSubject = PublishSubject.create();

    private final CopyOnWriteArrayList<Event> events = new CopyOnWriteArrayList<>();
    private final EventGenerator eventGenerator;
    private final MemberGenerator memberGenerator;

    public ReactiveEventService() {
        initializePools();
        this.eventGenerator = new EventGenerator();
        this.memberGenerator = new MemberGenerator();
    }

    private void initializePools() {
        UserGenerator userGenerator = new UserGenerator();
        PlacementGenerator placementGenerator = new PlacementGenerator();

        SimplePool<User> userPool = Pools.get(User.class);
        SimplePool<Placement> placementPool = Pools.get(Placement.class);

        if (userPool.size() == 0) {
            userPool.addAll(userGenerator.generate(500));
        }
        if (placementPool.size() == 0) {
            placementPool.addAll(placementGenerator.generate(30));
        }
    }

    public Observable<List<Event>> getEventsObservable() {
        return eventsSubject.hide();
    }

    public Observable<Notification> getNotifications() {
        return notificationSubject.hide();
    }

    public Observable<MemberUpdate> getMemberUpdates() {
        return memberUpdateSubject.hide();
    }

    public Observable<List<Event>> getFilteredEvents(MusicCompetitionGenre genre) {
        return eventsSubject
                .map(list -> list.stream()
                        .filter(e -> genre == null || e.getDescription().genre() == genre)
                        .toList())
                .subscribeOn(Schedulers.computation());
    }

    public Observable<Event> createEvent(String name, MusicCompetitionGenre genre, User organizer) {
        return Observable.fromCallable(() -> {
            Event event = eventGenerator.generate();
            event.getMembers().clear();

            if (name != null && !name.isBlank()) {
                event.setName(name);
            }

            if (genre != null) {
                var desc = event.getDescription();
                event.setDescription(new Description(
                        genre, desc.format(), desc.participantsLevel(), desc.description()
                ));
            }

            if (organizer != null) {
                Member organizerMember = new Member(organizer, MemberType.ORGANIZER);
                event.getMembers().add(organizerMember);
            }

            events.add(event);
            eventsSubject.onNext(new ArrayList<>(events));

            notificationSubject.onNext(new Notification(
                    NotificationType.EVENT_CREATED,
                    "Создано событие: " + event.getName()
            ));

            startMemberSimulation(event);

            return event;
        }).subscribeOn(Schedulers.io());
    }

    private void startMemberSimulation(Event event) {
        int totalMembers = 5 + (int)(Math.random() * 10);

        Observable.interval(500, 1500, TimeUnit.MILLISECONDS)
                .take(totalMembers)
                .subscribeOn(Schedulers.io())
                .subscribe(i -> {
                    Member newMember = memberGenerator.generate();
                    if (newMember.getMemberType() == MemberType.ORGANIZER) {
                        newMember = new Member(newMember.getUser(), MemberType.GUEST);
                    }
                    event.getMembers().add(newMember);

                    memberUpdateSubject.onNext(new MemberUpdate(event, newMember));
                    eventsSubject.onNext(new ArrayList<>(events));

                    String memberInfo = String.format("%s %s присоединился как %s",
                            newMember.getUser().getFirstName(),
                            newMember.getUser().getLastName(),
                            getMemberTypeName(newMember.getMemberType()));

                    notificationSubject.onNext(new Notification(
                            NotificationType.MEMBER_JOINED,
                            memberInfo + " к событию \"" + event.getName() + "\""
                    ));
                });
    }

    public Observable<Member> joinEvent(Event event, User user, MemberType memberType) {
        return Observable.fromCallable(() -> {
            Member member = new Member(user, memberType);
            event.getMembers().add(member);

            eventsSubject.onNext(new ArrayList<>(events));
            memberUpdateSubject.onNext(new MemberUpdate(event, member));

            notificationSubject.onNext(new Notification(
                    NotificationType.MEMBER_JOINED,
                    String.format("%s %s присоединился к \"%s\" как %s",
                            user.getFirstName(), user.getLastName(),
                            event.getName(), getMemberTypeName(memberType))
            ));

            return member;
        }).subscribeOn(Schedulers.io());
    }

    public void generateInitialEvents(int count) {
        Observable.range(0, count)
                .subscribeOn(Schedulers.io())
                .concatMap(i -> Observable.timer(300, TimeUnit.MILLISECONDS)
                        .map(t -> eventGenerator.generate()))
                .subscribe(event -> {
                    events.add(event);
                    eventsSubject.onNext(new ArrayList<>(events));
                    notificationSubject.onNext(new Notification(
                            NotificationType.EVENT_CREATED,
                            "Загружено событие: " + event.getName()
                    ));
                });
    }

    private String getMemberTypeName(MemberType type) {
        return switch (type) {
            case GUEST -> "Гость";
            case PERFORMER -> "Исполнитель";
            case ORGANIZER -> "Организатор";
            case PARTNER -> "Партнер";
        };
    }

    public List<Event> getCurrentEvents() {
        return new ArrayList<>(events);
    }

    public record Notification(NotificationType type, String message) {}

    public enum NotificationType {
        EVENT_CREATED, MEMBER_JOINED, EVENT_UPDATED
    }

    public record MemberUpdate(Event event, Member member) {}
}
