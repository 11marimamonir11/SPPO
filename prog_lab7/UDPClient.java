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
        this.channel = DatagramChannel.open();
        this.channel.configureBlocking(false);
    }

    public Response sendAndReceive(Request request) {
        try {
            byte[] requestData = SerializationUtils.serialize(request);
            ByteBuffer sendBuffer = ByteBuffer.wrap(requestData);
            channel.send(sendBuffer, serverAddress);

            ByteBuffer receiveBuffer = ByteBuffer.allocate(65535);
            SocketAddress receivedFrom = null;
            long startTime = System.currentTimeMillis();

            // Wait for 3 seconds maximum
            while (receivedFrom == null && (System.currentTimeMillis() - startTime) < 3000) {
                receivedFrom = channel.receive(receiveBuffer);
                if (receivedFrom == null) Thread.sleep(50);
            }

            if (receivedFrom == null) {
                System.out.println("Server is currently unavailable.");
                return null;
            }

            return (Response) SerializationUtils.deserialize(receiveBuffer.array());
        } catch (Exception e) {
            System.out.println("Network Error: " + e.getMessage());
            return null;
        }
    }
}