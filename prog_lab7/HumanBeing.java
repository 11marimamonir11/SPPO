package common.model;

import common.exception.ValidationException;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * Main entity stored in the collection.
 * Implements Comparable for default sorting.
 *
 * @param id               not null, >0, unique, auto-generated
 * @param name             not null, not empty
 * @param coordinates      not null
 * @param creationDate     not null, auto-generated
 * @param realHero         not null
 * @param hasToothpick     can be null
 * @param impactSpeed      must be > -64
 * @param soundtrackName   not null
 * @param minutesOfWaiting can be null
 * @param mood             not null
 * @param car              can be null
 */
public record HumanBeing(Integer id, String name, Coordinates coordinates, LocalDate creationDate, Boolean realHero,
                         Boolean hasToothpick, double impactSpeed, String soundtrackName, Integer minutesOfWaiting,
                         Mood mood, Car car) implements Comparable<HumanBeing>, Serializable {
    private static final long serialVersionUID = 1L;

    public HumanBeing {
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
