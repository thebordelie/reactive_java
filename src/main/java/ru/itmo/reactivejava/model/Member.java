package ru.itmo.reactivejava.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class Member {
    private User user;
    private MemberType memberType;
}
