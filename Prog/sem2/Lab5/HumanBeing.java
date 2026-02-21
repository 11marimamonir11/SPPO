package model;

import exception.ValidationException;

import java.time.LocalDate;

/**
 * Main entity stored in the collection.
 * Implements Comparable for default sorting.
 */
public class HumanBeing implements Comparable<HumanBeing> {

    private final Integer id; // not null, >0, unique, auto-generated
    private final String name; // not null, not empty
    private final Coordinates coordinates; // not null
    private final LocalDate creationDate; // not null, auto-generated
    private final Boolean realHero; // not null
    private final Boolean hasToothpick; // can be null
    private final double impactSpeed; // must be > -64
    private final String soundtrackName; // not null
    private final Integer minutesOfWaiting; // can be null
    private final Mood mood; // not null
    private final Car car; // can be null

    public HumanBeing(
            Integer id,
            String name,
            Coordinates coordinates,
            LocalDate creationDate,
            Boolean realHero,
            Boolean hasToothpick,
            double impactSpeed,
            String soundtrackName,
            Integer minutesOfWaiting,
            Mood mood,
            Car car
    ) {
        // validations ----
        if (id == null) throw new ValidationException("id cannot be null");
        if (id <= 0) throw new ValidationException("id must be > 0");

        if (name == null) throw new ValidationException("name cannot be null");
        if (name.isBlank()) throw new ValidationException("name cannot be empty");

        if (coordinates == null) throw new ValidationException("coordinates cannot be null");

        if (creationDate == null) throw new ValidationException("creationDate cannot be null");

        if (realHero == null) throw new ValidationException("realHero cannot be null");

        if (impactSpeed <= -64) throw new ValidationException("impactSpeed must be > -64");

        if (soundtrackName == null) throw new ValidationException("soundtrackName cannot be null");

        if (mood == null) throw new ValidationException("mood cannot be null");

        // assign fields
        this.id = id;
        this.name = name;
        this.coordinates = coordinates;
        this.creationDate = creationDate;
        this.realHero = realHero;
        this.hasToothpick = hasToothpick;
        this.impactSpeed = impactSpeed;
        this.soundtrackName = soundtrackName;
        this.minutesOfWaiting = minutesOfWaiting;
        this.mood = mood;
        this.car = car;
    }

    // getters ----
    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public Boolean getRealHero() {
        return realHero;
    }

    public Boolean getHasToothpick() {
        return hasToothpick;
    }

    public double getImpactSpeed() {
        return impactSpeed;
    }

    public String getSoundtrackName() {
        return soundtrackName;
    }

    public Integer getMinutesOfWaiting() {
        return minutesOfWaiting;
    }

    public Mood getMood() {
        return mood;
    }

    public Car getCar() {
        return car;
    }

    // Default sorting: by impactSpeed, then by id.

    @Override
    public int compareTo(HumanBeing other) {
        int cmp = Double.compare(this.impactSpeed, other.impactSpeed);
        if (cmp != 0) return cmp;
        return this.id.compareTo(other.id);
    }

    @Override
    public String toString() {
        return "HumanBeing{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", coordinates=" + coordinates +
                ", creationDate=" + creationDate +
                ", realHero=" + realHero +
                ", hasToothpick=" + hasToothpick +
                ", impactSpeed=" + impactSpeed +
                ", soundtrackName='" + soundtrackName + '\'' +
                ", minutesOfWaiting=" + minutesOfWaiting +
                ", mood=" + mood +
                ", car=" + car +
                '}';
    }
}
