package ru.itmo.reactivejava.generator;

import ru.itmo.reactivejava.model.Member;
import ru.itmo.reactivejava.model.User;

import java.util.HashMap;

// TODO Петя
public class MemberGenerator extends AbstractGenerator<Member> {
    public MemberGenerator(HashMap<Class, Object> properties) {
        super(properties);

    }

    @Override
    public Member generateObject() {
        UserGenerator userGenerator = (UserGenerator) properties.get(User.class);
        User user = userGenerator.generateObject();
        return null;
    }
}
