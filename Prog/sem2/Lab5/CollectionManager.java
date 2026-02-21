package manager;

import model.Car;
import model.Coordinates;
import model.HumanBeing;
import model.Mood;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;
import java.util.Stack;

/**
 * Stores and manages the collection of HumanBeing objects.
 * This is the "collection context" in the Command Pattern architecture.
 */

public class CollectionManager {

    private final Stack<HumanBeing> stack = new Stack<>();
    private final LocalDate initializationDate = LocalDate.now();
    private final String fileName;
    private int nextId = 1;

    public CollectionManager(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }

    // existing methods stay the same

    public Stack<HumanBeing> getStack() {
        return stack;
    }

    public LocalDate getInitializationDate() {
        return initializationDate;
    }

    // Generates a unique id for new elements (used when adding from console).
    public int generateNextId() {
        return nextId++;
    }

    /**
     * After loading from file, setting nextId to (maxId + 1).
     * then call this after reading CSV.
     */
    public void updateNextIdFromCollection() {
        int maxId = stack.stream()
                .map(HumanBeing::getId)
                .max(Integer::compareTo)
                .orElse(0);
        nextId = maxId + 1;
    }

    public Optional<HumanBeing> findById(int id) {
        return stack.stream().filter(h -> h.getId() == id).findFirst();
    }

    public boolean removeById(int id) {
        return stack.removeIf(h -> h.getId() == id);
    }

    public void clear() {
        stack.clear();
    }

    public void loadFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {

            String line;

            while ((line = reader.readLine()) != null) {

                if (line.trim().isEmpty()) continue;

                String[] data = line.split(",", -1);

                if (data.length < 13) {
                    String[] fixed = new String[13];
                    for (int i = 0; i < 13; i++) fixed[i] = "";
                    System.arraycopy(data, 0, fixed, 0, data.length);
                    data = fixed;
                }

                HumanBeing hb = new HumanBeing(
                        Integer.parseInt(data[0]),
                        data[1],
                        new Coordinates(
                                Integer.parseInt(data[2]),
                                Integer.parseInt(data[3])
                        ),
                        LocalDate.parse(data[4]),
                        Boolean.parseBoolean(data[5]),
                        data[6].isEmpty() ? null : Boolean.parseBoolean(data[6]),
                        Double.parseDouble(data[7]),
                        data[8],
                        data[9].isEmpty() ? null : Integer.parseInt(data[9]),
                        Mood.valueOf(data[10]),
                        data[11].isEmpty() ? null :
                                new Car(
                                        data[11],
                                        data[12].isEmpty() ? null : Boolean.parseBoolean(data[12])
                                )
                );

                stack.add(hb);
            }

            updateNextIdFromCollection();

            System.out.println("Loaded elements: " + stack.size());

        } catch (Exception e) {
            System.out.println("Error while loading file: " + e.getMessage());
        }
    }

    public void saveToFile() {
        try (FileOutputStream fos = new FileOutputStream(fileName)) {

            for (HumanBeing hb : stack) {
                String line = toCsv(hb) + "\n";
                fos.write(line.getBytes());
            }

            System.out.println("Collection saved successfully.");

        } catch (IOException e) {
            System.out.println("Error while saving file: " + e.getMessage());
        }
    }

    private String toCsv(HumanBeing hb) {
        String carName = "";
        String carCool = "";

        if (hb.getCar() != null) {
            carName = hb.getCar().getName();
            carCool = (hb.getCar().getCool() == null) ? "" : hb.getCar().getCool().toString();
        }

        String hasToothpick = (hb.getHasToothpick() == null) ? "" : hb.getHasToothpick().toString();
        String minutesOfWaiting = (hb.getMinutesOfWaiting() == null) ? "" : hb.getMinutesOfWaiting().toString();

        return hb.getId() + "," +
                hb.getName() + "," +
                hb.getCoordinates().getX() + "," +
                hb.getCoordinates().getY() + "," +
                hb.getCreationDate() + "," +
                hb.getRealHero() + "," +
                hasToothpick + "," +
                hb.getImpactSpeed() + "," +
                hb.getSoundtrackName() + "," +
                minutesOfWaiting + "," +
                hb.getMood() + "," +
                carName + "," +
                carCool;
    }

    public void addHumanBeing(
            String name,
            Coordinates coordinates,
            Boolean realHero,
            Boolean hasToothpick,
            double impactSpeed,
            String soundtrackName,
            Integer minutesOfWaiting,
            Mood mood,
            Car car
    ) {
        int id = generateNextId();
        LocalDate creationDate = LocalDate.now();

        HumanBeing hb = new HumanBeing(
                id,
                name,
                coordinates,
                creationDate,
                realHero,
                hasToothpick,
                impactSpeed,
                soundtrackName,
                minutesOfWaiting,
                mood,
                car
        );

        stack.push(hb);
        System.out.println("Added element with id=" + id);
    }

    public boolean updateById(
            int id,
            String name,
            Coordinates coordinates,
            Boolean realHero,
            Boolean hasToothpick,
            double impactSpeed,
            String soundtrackName,
            Integer minutesOfWaiting,
            Mood mood,
            Car car
    ) {
        Optional<HumanBeing> oldOpt = findById(id);
        if (oldOpt.isEmpty()) {
            return false;
        }

        HumanBeing old = oldOpt.get();

        HumanBeing updated = new HumanBeing(
                id,                         // keep same id
                name,
                coordinates,
                old.getCreationDate(),      // keep same creationDate
                realHero,
                hasToothpick,
                impactSpeed,
                soundtrackName,
                minutesOfWaiting,
                mood,
                car
        );

        // remove old element and add updated
        removeById(id);
        stack.push(updated);

        System.out.println("Updated element with id=" + id);
        return true;
    }


}
