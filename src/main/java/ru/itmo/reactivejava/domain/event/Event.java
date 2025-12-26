package ru.itmo.reactivejava.domain.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.SneakyThrows;
import ru.itmo.reactivejava.domain.member.Member;
import ru.itmo.reactivejava.domain.placement.Placement;
import ru.itmo.reactivejava.shared.config.DelayConfig;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@AllArgsConstructor
@Data
public class Event {
    private long id;
    private String name;
    private LocalDateTime dateTime;
    private Description description;
    private Placement placement;
    private List<Member> members;

    @SneakyThrows
    public Placement getPlacement() {
        if (DelayConfig.isEnabled()) {
            Thread.sleep(ThreadLocalRandom.current().nextInt(10));
        }
        return placement;
    }
}
