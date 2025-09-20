package ru.itmo.reactivejava.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.Collection;

@Data
public class Event {
    private ArrayList<Member> members;

    public void addMembers(Collection<Member> members) {
        this.members.addAll(members);
    }
}
