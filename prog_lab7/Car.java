package common.model;

import common.exception.ValidationException;

import java.io.Serializable;

/**
 * @param name cannot be null
 * @param cool can be null
 */
public record Car(String name, Boolean cool) implements Serializable {
    private static final long serialVersionUID = 1L;

    public Car {
        if (name == null) {
            throw new ValidationException("Car.name cannot be null");
        }
    }

    @Override
    public String toString() {
        return "Car{name='" + name + "', cool=" + cool + "}";
    }
}
