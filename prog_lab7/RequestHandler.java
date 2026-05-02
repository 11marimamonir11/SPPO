package server;

import common.model.HumanBeing;
import common.network.Request;
import common.network.Response;
import server.manager.CollectionManager;
import server.manager.DatabaseManager;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class RequestHandler {
    private final CollectionManager collectionManager;
    private final DatabaseManager databaseManager; // New dependency

    public RequestHandler(CollectionManager collectionManager, DatabaseManager databaseManager) {
        this.collectionManager = collectionManager;
        this.databaseManager = databaseManager;
    }

    public Response handle(Request request) {
        String command = request.commandName();
        HumanBeing obj = request.objectArgument();
        String arg = request.commandStringArgument();
        String user = request.username();
        String pass = request.password();

        try {
            // Authentication & Registration
            // Check if user exists. If not, register them. If exists, check password.
            if (!databaseManager.authenticate(user, pass)) {
                return new Response("Authentication failed. Wrong password.", null);
            }

            // 2. Execute Commands
            switch (command) {
                case "help":
                    return new Response("Available commands: help, info, show, add, update, remove_by_id, clear, execute_script, exit, add_if_min, shuffle, remove_greater, remove_all_by_minutes_of_waiting, filter_by_impact_speed, print_descending", null);

                case "info":
                    return new Response(collectionManager.getInfo(), null);

                case "show":
                    // sorted by name
                    List<HumanBeing> sortedList = collectionManager.getSortedStack();
                    if (sortedList.isEmpty()) return new Response("Collection is empty.", null);
                    return new Response("Collection elements:", sortedList);

                case "add":
                    // Add to DB first, then to memory
                    HumanBeing hbWithId = databaseManager.insertHumanBeing(obj, user);
                    collectionManager.addHumanBeing(hbWithId);
                    return new Response("Element successfully added with ID: " + hbWithId.id(), null);

                case "update":
                    int idToUpdate = Integer.parseInt(arg);
                    // Modify only objects belonging to them
                    if (databaseManager.updateHumanBeing(idToUpdate, obj, user)) {
                        // Create object for memory update (must have the ID)
                        HumanBeing updatedHb = new HumanBeing(idToUpdate, obj.name(), obj.coordinates(),
                                java.time.LocalDate.now(), obj.realHero(), obj.hasToothpick(),
                                obj.impactSpeed(), obj.soundtrackName(), obj.minutesOfWaiting(),
                                obj.mood(), obj.car());
                        collectionManager.updateHumanBeing(updatedHb);
                        return new Response("Element updated successfully.", null);
                    } else {
                        return new Response("Error: You don't own this object or it doesn't exist.", null);
                    }

                case "remove_by_id":
                    int idToRemove = Integer.parseInt(arg);
                    if (databaseManager.deleteHumanBeing(idToRemove, user)) {
                        collectionManager.removeById(idToRemove);
                        return new Response("Element removed.", null);
                    } else {
                        return new Response("Error: You don't own this object or it doesn't exist.", null);
                    }

                case "clear":
                    int count = databaseManager.clearUserObjects(user);
                    collectionManager.getStack().removeIf(hb -> {
                        try {
                            return databaseManager.isOwner(hb.id(), user);
                        } catch (Exception e) {
                            return false;
                        }
                    });

                    return new Response("Cleared " + count + " elements owned by you.", null);

                case "add_if_min":
                    HumanBeing min = collectionManager.getMin();
                    if (min == null || obj.compareTo(min) < 0) {
                        HumanBeing added = databaseManager.insertHumanBeing(obj, user);
                        collectionManager.addHumanBeing(added);
                        return new Response("Element was minimum. Added successfully.", null);
                    }
                    return new Response("Element is not minimum. Not added.", null);

                case "remove_greater":
                    // Use Stream API to find which ones to remove
                    List<Integer> idsToRemove = collectionManager.getStack().stream()
                            .filter(hb -> hb.compareTo(obj) > 0)
                            .map(HumanBeing::id)
                            .collect(Collectors.toList());

                    int removed = 0;
                    for (int id : idsToRemove) {
                        if (databaseManager.deleteHumanBeing(id, user)) {
                            collectionManager.removeById(id);
                            removed++;
                        }
                    }
                    return new Response("Removed " + removed + " elements that you owned.", null);

                case "filter_by_impact_speed":
                    double speed = Double.parseDouble(arg);
                    List<HumanBeing> filtered = collectionManager.filterByImpactSpeed(speed);
                    return new Response("Filtered elements:", filtered);

                case "print_descending":
                    List<HumanBeing> desc = collectionManager.getSortedStack();
                    Collections.reverse(desc);
                    return new Response("Descending order:", desc);

                default:
                    return new Response("Unknown command: " + command, null);
            }
        } catch (NumberFormatException e) {
            return new Response("Error: Argument must be a number.", null);
        } catch (Exception e) {
            return new Response("Critical Server Error: " + e.getMessage(), null);
        }
    }
}