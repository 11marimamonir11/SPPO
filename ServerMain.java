package server;

import common.model.HumanBeing;
import server.manager.CollectionManager;
import server.manager.DatabaseManager;

import java.util.List;
import java.util.Scanner;

public class ServerMain {
    public static void main(String[] args) {
        String dbUser = "s503339";
        String dbPass = "urxH+1591";

        try {
            System.out.println("--- Starting Server for Lab 7 ---");
            DatabaseManager databaseManager = new DatabaseManager(dbUser, dbPass);
            CollectionManager collectionManager = new CollectionManager();

            //Load collection from Database into memory at startup
            List<HumanBeing> existingData = databaseManager.loadAll();
            for (HumanBeing hb : existingData) {
                collectionManager.addHumanBeing(hb);
            }
            System.out.println("Successfully loaded " + existingData.size() + " elements from the database.");

            // Setup Request Handler and Server
            RequestHandler requestHandler = new RequestHandler(collectionManager, databaseManager);
            Server server = new Server(8080, requestHandler);

            //start network server in a separate thread
            new Thread(server::start).start();

            System.out.println("Server is active on port 8080.");
            System.out.println("Commands: 'exit' to shut down server.");

            // Server console loop
            Scanner scanner = new Scanner(System.in);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.equalsIgnoreCase("exit")) {
                    System.out.println("Shutting down server...");
                    System.exit(0);
                }
            }

        } catch (Exception e) {
            System.err.println("CRITICAL ERROR: Failed to start the server.");
            System.err.println("Reason: " + e.getMessage());
        }
    }
}