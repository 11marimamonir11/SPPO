package server.manager;

import common.model.HumanBeing;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Stack;
import java.util.stream.Collectors;


//Memory-only management for collection reading

public class CollectionManager {

    private final Stack<HumanBeing> stack = new Stack<>();
    private final LocalDate initializationDate = LocalDate.now();

    public CollectionManager() {
    }
    //All commands that get data should work with memory. Returns the stack.

    public synchronized Stack<HumanBeing> getStack() {
        return stack;
    }

    public synchronized List<HumanBeing> getSortedStack() {
        return stack.stream()
                .sorted(Comparator.comparing(HumanBeing::name))
                .collect(Collectors.toList());
    }
    public synchronized LocalDate getInitializationDate() {
        return initializationDate;
    }
    public synchronized Optional<HumanBeing> findById(int id) {
        return stack.stream().filter(h -> h.id().equals(id)).findFirst();
    }

    //Memory is only updated if the DB operation is successful.
    public synchronized boolean removeById(int id) {
        return stack.removeIf(h -> h.id().equals(id));
    }

    public synchronized void clear() {
        stack.clear();
    }

    //Adds an object to memory, ID and CreationDate must be already set by the Database before calling this.

    public synchronized void addHumanBeing(HumanBeing hb) {
        stack.push(hb);
    }

    public synchronized boolean updateHumanBeing(HumanBeing updatedHb) {
        Optional<HumanBeing> oldOpt = findById(updatedHb.id());
        if (oldOpt.isPresent()) {
            removeById(updatedHb.id());
            stack.push(updatedHb);
            return true;
        }
        return false;
    }
    //'info' command.
    public synchronized String getInfo() {
        return "Type: Stack<HumanBeing>\n" +
                "Initialization date: " + initializationDate + "\n" +
                "Number of elements: " + stack.size();
    }
    //Filter by impact speed using Stream API.
    public synchronized List<HumanBeing> filterByImpactSpeed(double speed) {
        return stack.stream()
                .filter(hb -> hb.impactSpeed() == speed)
                .collect(Collectors.toList());
    }
    public synchronized HumanBeing getMin() {
        if (stack.isEmpty()) return null;
        // Uses the compareTo method inside your HumanBeing class
        return stack.stream()
                .min(HumanBeing::compareTo)
                .orElse(null);
    }
}