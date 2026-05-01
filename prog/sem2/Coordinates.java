package common.model;

import common.exception.ValidationException;

import java.io.Serializable;


public class Coordinates implements Serializable {
    private static final long serialVersionUID = 1L;

    private final Integer x; // not null
    private final int y;

    public Coordinates(Integer x, int y) {
        if (x == null) {
            throw new ValidationException("coordinates.x cannot be null");
        }
        this.x = x;
        this.y = y;
    }

    public Integer getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public String toString() {
        return "Coordinates{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
