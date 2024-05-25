package ru.itmo.general.models;

import java.io.Serializable;

public class LocactioN implements Serializable {

    private Long x; // Поле не может быть null
    private Integer y; // Поле не может быть null
    private String name; // Длина строки не должна быть больше 502, Поле не может быть null



    public LocactioN(Long x, Integer y, String name) {
        this.x = x;
        this.y = y;
        this.name = name;
    }

    public LocactioN(String s) {
        try {
            try { this.x = Long.parseLong(s.split(";")[0]); } catch (NumberFormatException ignored) {}
            try { this.y = Integer.parseInt(s.split(";")[1]); } catch (NumberFormatException ignored) {}
        } catch (ArrayIndexOutOfBoundsException ignored) {}
    }

    public boolean validate() {
        if (x == null || y == null) return false;
        return name.length() <= 502;
    }

    public Long getX() {
        return x;
    }

    public void setX(Long x) {
        this.x = x;
    }

    public Integer getY() {
        return y;
    }

    public void setY(Integer y) {
        this.y = y;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    @Override
    public String toString() {
        return "(" + x + "," + y + ", \"" + name + "\")";
    }


}

