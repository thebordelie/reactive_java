package ru.itmo.reactivejava.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class User {
    private int id;
    private String firstName;
    private String lastName;
    private int age;
    private String email;
}
