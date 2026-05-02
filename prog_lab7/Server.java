package server;

import common.network.Request;
import common.network.Response;
import common.network.SerializationUtils;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class Server {
    private static final Logger logger = Logger.getLogger(Server.class.getName());
    private final int port;
    private final RequestHandler requestHandler;
    private final ExecutorService processingPool = Executors.newFixedThreadPool(10);
    private final ExecutorService sendingPool = Executors.newFixedThreadPool(10);

    public Server(int port, RequestHandler requestHandler) {
        this.port = port;
        this.requestHandler = requestHandler;
    }

    public void start() {
        // Use a try-with-resources to ensure the socket closes properly
        try (DatagramSocket socket = new DatagramSocket(port)) {
            logger.info("Server started successfully on port " + port);

            while (true) {
                byte[] buffer = new byte[65535];
                DatagramPacket receivePacket = new DatagramPacket(buffer, buffer.length);

                // Receive is blocking
                socket.receive(receivePacket);

                // Multi-threaded reading (New Thread for every request)
                new Thread(() -> {
                    handleRequest(receivePacket, socket);
                }).start();
            }
        } catch (Exception e) {
            logger.severe("Server Critical Error: " + e.getMessage());
        }
    }

    private void handleRequest(DatagramPacket receivePacket, DatagramSocket socket) {
        // Multi-threaded processing using a Fixed Thread Pool
        processingPool.submit(() -> {
            try {
                Request request = (Request) SerializationUtils.deserialize(receivePacket.getData());
                logger.info("Processing command: " + request.commandName() + " from " + request.username());

                // Handle the logic via the RequestHandler
                Response response = requestHandler.handle(request);

                // Requirement 3: Multi-threaded sending using a Fixed Thread Pool
                sendingPool.submit(() -> {
                    try {
                        byte[] sendData = SerializationUtils.serialize(response);
                        DatagramPacket sendPacket = new DatagramPacket(
                                sendData, sendData.length,
                                receivePacket.getAddress(), receivePacket.getPort()
                        );
                        socket.send(sendPacket);
                        logger.info("Response sent to " + receivePacket.getAddress());
                    } catch (Exception e) {
                        logger.warning("Error sending response: " + e.getMessage());
                    }
                });

            } catch (Exception e) {
                logger.warning("Error processing request: " + e.getMessage());
            }
        });
    }
}