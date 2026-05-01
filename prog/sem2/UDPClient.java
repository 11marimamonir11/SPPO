package client;

import common.network.Request;
import common.network.Response;
import common.network.SerializationUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

public class UDPClient {
    private final DatagramChannel channel;
    private final SocketAddress serverAddress;

    public UDPClient(String host, int port) throws IOException {
        this.serverAddress = new InetSocketAddress(host, port);

        //  Use network channels in non-blocking mode
        this.channel = DatagramChannel.open();
        this.channel.configureBlocking(false);
    }

    public Response sendAndReceive(Request request) {
        try {
            // 1. Serialize the Request object into bytes
            byte[] requestData = SerializationUtils.serialize(request);
            ByteBuffer sendBuffer = ByteBuffer.wrap(requestData);

            // 2. Send the bytes to the server
            channel.send(sendBuffer, serverAddress);

            // 3. Prepare a buffer to receive the server's answer
            ByteBuffer receiveBuffer = ByteBuffer.allocate(65535);
            SocketAddress receivedFrom = null;

            // Handle server unavailability (Timeout)
            // Because it is non-blocking, we loop for a maximum of 3 seconds waiting for a reply
            long startTime = System.currentTimeMillis();
            while (receivedFrom == null && (System.currentTimeMillis() - startTime) < 3000) {
                receivedFrom = channel.receive(receiveBuffer);
                if (receivedFrom == null) {
                    Thread.sleep(50); // Sleep for 50ms so we don't freeze the CPU
                }
            }

            if (receivedFrom == null) {
                System.out.println("Server is currently unavailable. Please try again later.");
                return null;
            }

            // 5. Deserialize the received bytes back into a Response object
            return (Response) SerializationUtils.deserialize(receiveBuffer.array());

        } catch (Exception e) {
            System.out.println("Network Error: " + e.getMessage());
            return null;
        }
    }
}