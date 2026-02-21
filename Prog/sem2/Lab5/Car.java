package model;

import exception.ValidationException;

public class Car {

    private final String name;   // cannot be null
    private final Boolean cool;  // can be null

    public Car(String name, Boolean cool) {
        if (name == null) {
            throw new ValidationException("Car.name cannot be null");
        }
        this.name = name;
        this.cool = cool;
    }

    public String getName() {
        return name;
    }

    public Boolean getCool() {
        return cool;
    }

    @Override
    public String toString() {
        return "Car{name='" + name + "', cool=" + cool + "}";
    }
}
