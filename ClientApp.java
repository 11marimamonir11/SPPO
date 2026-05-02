package client;

import common.model.Car;
import common.model.Coordinates;
import common.model.HumanBeing;
import common.model.Mood;
import common.network.Request;
import common.network.Response;
import util.InputManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class ClientApp {
    private final UDPClient client;
    private final InputManager inputManager;
    private final Set<String> activeScripts; // Prevents infinite script recursion

    // Credentials stored after login
    private String username;
    private String password;

    public ClientApp(UDPClient client) {
        this.client = client;
        // Using the console scanner for the initial input
        this.inputManager = new InputManager(new Scanner(System.in));
        this.activeScripts = new HashSet<>();
    }

    //Entry point for the client application.Authenticate user before allowing commands.

    public void start() {
        System.out.println("--- Welcome to the HumanBeing Collection Manager ---");

        // 1. Requirement: Authentication/Registration
        System.out.println("Please log in or register (credentials will be sent to server).");
        this.username = inputManager.readNonEmptyString("Enter Username");
        this.password = inputManager.readNonEmptyString("Enter Password");

        System.out.println("Interactive mode started. Type 'help' for commands or 'exit' to quit.");

        // 2. Main command loop
        while (true) {
            System.out.print("> ");
            // Check if there is more input (handles Ctrl+D)
            if (!inputManager.getScanner().hasNextLine()) break;

            String line = inputManager.getScanner().nextLine().trim();
            processCommand(line);
        }
    }

    private void processCommand(String inputLine) {
        if (inputLine.isEmpty()) return;

        String[] parts = inputLine.split(" ", 2);
        String commandName = parts[0].toLowerCase();
        String commandArg = parts.length > 1 ? parts[1].trim() : "";

        // Local commands
        if (commandName.equals("exit")) {
            System.out.println("Exiting client...");
            System.exit(0);
        }

        if (commandName.equals("save")) {
            System.out.println("Error: The 'save' command is only available on the server console.");
            return;
        }

        if (commandName.equals("execute_script")) {
            executeScript(commandArg);
            return;
        }

        // Object building for specific commands
        HumanBeing objectArg = null;
        if (commandName.equals("add") || commandName.equals("add_if_min") ||
                commandName.equals("remove_greater") || commandName.equals("update")) {

            if (commandName.equals("update") && commandArg.isEmpty()) {
                System.out.println("Usage: update <id>");
                return;
            }

            System.out.println("Please enter the object details:");
            try {
                objectArg = readHumanBeing();
            } catch (Exception e) {
                System.out.println("Input error. Aborting command.");
                return;
            }
        }

        // Send login/password with EVERY request
        Request request = new Request(commandName, commandArg, objectArg, username, password);

        // Communication with server
        Response response = client.sendAndReceive(request);

        // Handle Server Response
        if (response != null) {
            System.out.println(response.message());
            // Requirement: Users can view all objects, but modified list might be sent back
            if (response.collection() != null && !response.collection().isEmpty()) {
                for (HumanBeing hb : response.collection()) {
                    System.out.println(hb);
                }
            }
        }
    }

    private void executeScript(String scriptName) {
        if (scriptName.isEmpty()) {
            System.out.println("Usage: execute_script <file_name>");
            return;
        }

        File file = new File(scriptName);
        try {
            String canonicalPath = file.getCanonicalPath();
            if (activeScripts.contains(canonicalPath)) {
                System.out.println("Recursion detected: script " + scriptName + " is already running.");
                return;
            }

            activeScripts.add(canonicalPath);
            Scanner oldScanner = inputManager.getScanner();

            try (Scanner fileScanner = new Scanner(file)) {
                inputManager.setScanner(fileScanner);
                while (fileScanner.hasNextLine()) {
                    String line = fileScanner.nextLine().trim();
                    System.out.println("> " + line);
                    processCommand(line);
                }
            } finally {
                inputManager.setScanner(oldScanner);
                activeScripts.remove(canonicalPath);
            }
        } catch (FileNotFoundException e) {
            System.out.println("Script file not found: " + scriptName);
        } catch (Exception e) {
            System.out.println("Error executing script: " + e.getMessage());
        }
    }

    private HumanBeing readHumanBeing() {
        String name = inputManager.readNonEmptyString("Enter name");
        int x = inputManager.readInt("Enter coordinates.x");
        int y = inputManager.readInt("Enter coordinates.y");
        Coordinates coordinates = new Coordinates(x, y);
        boolean realHero = inputManager.readBoolean("Enter realHero");
        Boolean hasToothpick = inputManager.readBooleanNullable("Enter hasToothpick");

        double impactSpeed;
        while (true) {
            impactSpeed = inputManager.readDouble("Enter impactSpeed (> -64)");
            if (impactSpeed > -64) break;
            System.out.println("impactSpeed must be > -64. Try again.");
        }

        String soundtrackName = inputManager.readNonEmptyString("Enter soundtrackName");
        Integer minutesOfWaiting = inputManager.readIntNullable("Enter minutesOfWaiting");
        Mood mood = inputManager.readEnum("Enter mood", Mood.class);

        Car car = null;
        boolean hasCar = inputManager.readBoolean("Do you want to enter car? (true = yes, false = no)");
        if (hasCar) {
            String carName = inputManager.readNonEmptyString("Enter car.name");
            Boolean carCool = inputManager.readBooleanNullable("Enter car.cool");
            car = new Car(carName, carCool);
        }

        // Dummy ID and Date (Server generates real ones in DB)
        return new HumanBeing(
                0, name, coordinates, java.time.LocalDate.now(), realHero,
                hasToothpick, impactSpeed, soundtrackName, minutesOfWaiting, mood, car
        );
    }
}