package ru.itmo.reactivejava.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class Placement {
    private int okato;
    private int capacity;
    private String placement;
    private String address;
}
