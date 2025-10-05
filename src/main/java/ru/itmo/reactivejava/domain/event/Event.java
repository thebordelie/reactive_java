package ru.itmo.reactivejava.domain.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.itmo.reactivejava.domain.member.Member;
import ru.itmo.reactivejava.domain.placement.Placement;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@Data
public class Event {
    private long id;
    private String name;
    private LocalDateTime dateTime;
    private Description description;
    private Placement placement;
    private List<Member> members;
}
