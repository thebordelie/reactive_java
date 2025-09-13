package ru.itmo.reactivejava;

import ru.itmo.reactivejava.generator.MemberGenerator;
import ru.itmo.reactivejava.generator.UserGenerator;
import ru.itmo.reactivejava.model.User;

import java.util.ArrayList;
import java.util.HashMap;

public class App {
    public static void main(String[] args) {
        UserGenerator userGenerator = new UserGenerator();
        User user = userGenerator.generateObject();
        user.getId();

        HashMap<Class, Object> hashMap = new HashMap<>();
        hashMap.put(User.class, userGenerator);
        MemberGenerator memberGenerator = new MemberGenerator(hashMap);
        memberGenerator.generateObject();

    }
}
