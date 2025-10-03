package ru.itmo.reactivejava.features.generation;

import lombok.NoArgsConstructor;
import ru.itmo.reactivejava.domain.member.Member;
import ru.itmo.reactivejava.domain.member.MemberType;
import ru.itmo.reactivejava.shared.user.User;
import ru.itmo.reactivejava.features.pool.Pools;
import ru.itmo.reactivejava.features.pool.SimplePool;

import java.util.Collection;
import java.util.Random;

@NoArgsConstructor
public class MemberGenerator implements Generator<Member> {

    SimplePool<User> usersSimplePool = Pools.get(User.class);

    @Override
    public Member generate() {
        MemberType memberType = MemberType.values()[new Random().nextInt(0, MemberType.values().length)];
        User user = usersSimplePool.getRandom();
        return new Member(user, memberType);
    }

    @Override
    public Collection<Member> generate(int count) {
        Collection<User> users = usersSimplePool.getRandom(count);
        return users.stream().map(u -> {
            MemberType memberType = MemberType.values()[new Random().nextInt(0, MemberType.values().length)];
            return new Member(u, memberType);
        }).toList();
    }
}
