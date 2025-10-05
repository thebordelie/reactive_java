package ru.itmo.reactivejava.domain.member;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.itmo.reactivejava.shared.user.User;

@AllArgsConstructor
@Data
public class Member {
    private User user;
    private MemberType memberType;
}
