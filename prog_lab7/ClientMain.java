package client;

public class ClientMain {
    public static void main(String[] args) {
        try {
            // Helios server uses localhost or 127.0.0.1 if running on same machine
            UDPClient client = new UDPClient("localhost", 8080);
            ClientApp app = new ClientApp(client);
            app.start();
        } catch (Exception e) {
            System.out.println("Error starting client: " + e.getMessage());
        }
    }
}