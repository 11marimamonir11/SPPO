package client;

public class ClientMain {
    public static void main(String[] args) {
        try {
            // Connect to the server on localhost port 8080
            UDPClient client = new UDPClient("localhost", 8080);

            // Start the interactive terminal
            ClientApp app = new ClientApp(client);
            app.start();

        } catch (Exception e) {
            System.out.println("Error starting client: " + e.getMessage());
        }
    }
}