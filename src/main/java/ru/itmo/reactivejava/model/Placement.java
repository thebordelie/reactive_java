package ru.itmo.reactivejava.model;

import lombok.Data;

@Data
public class Placement {
    private String okato;
    private String address;
    private int capacity;

    public Placement(String okato, String address, int capacity) {
        setOkato(okato);
        this.address = address;
        this.capacity = capacity;
    }

    public void setOkato(String okato) {
        if (okato == null || !okato.matches("\\d{1,11}")) {
            throw new IllegalArgumentException("Wrong okato mask");
        }
        this.okato = okato;
    }
}
