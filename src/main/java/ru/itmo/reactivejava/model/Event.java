package ru.itmo.reactivejava.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
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
    private List<User> users;
}
