package common.model;

import common.exception.ValidationException;

import java.io.Serializable;


/**
 * @param x not null
 */
public record Coordinates(Integer x, int y) implements Serializable {
    private static final long serialVersionUID = 1L;

    public Coordinates {
        if (x == null) {
            throw new ValidationException("coordinates.x cannot be null");
        }
    }

    @Override
    public String toString() {
        return "Coordinates{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
