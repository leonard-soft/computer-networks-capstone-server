package org.example.udp;

import org.example.dto.DataTransferDTO;
import org.example.dto.PlayerConnection;
import org.example.logs.ManageLogs;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;

public class UdpService {

    private final DatagramSocket socket;
    private final ManageLogs manageLogs;
    // Map to store data for the connection between players
    private final Map<Integer, PlayerConnection> connections = new HashMap<>();

    /**
     * Constructor to initialize the socket as soon as a new instance is created,
     * so as not to close and create the connection
     * @throws SocketException to handle possible errors when creating the socket
     */
    public UdpService() throws SocketException {
        this.socket = new DatagramSocket();
        this.manageLogs = new ManageLogs();
    }

    /**
     * It is the function responsible for sending data between players using the UDP protocol.
     * @param data It is the object where the values necessary for the multiplayer to work are found.
     */
    public void clientUdp(DataTransferDTO data) {
        PlayerConnection player = connections.get(data.getIdPlayer());

        if (player == null) {
            manageLogs.saveLog("WARN", "No connection found for player with ID: " + data.getIdPlayer());
            return;
        }

        try {
            byte[] buffer = objectToBytes(data);
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, player.getIp(), player.getPort());
            socket.send(packet);
            manageLogs.saveLog("INFO", "Data sent to player " + data.getIdPlayer() +
                    " (" + player.getIp() + ":" + player.getPort() + ")");
        } catch (IOException e) {
            manageLogs.saveLog("ERROR", "Error sending UDP packet to player " +
                    data.getIdPlayer() + ": " + e.getMessage());
        }
    }

    /**
     * Function responsible for obtaining the byte of the object to be sent
     * @param object It is the class that contains the values to move the player remotely
     * @return a byte array
     */
    private byte[] objectToBytes(Object object) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream)) {
            objectOutputStream.writeObject(object);
            return outputStream.toByteArray();
        } catch (IOException e) {
            manageLogs.saveLog("ERROR", "Error serializing object: " + e.getMessage());
            return new byte[0];
        }
    }

    /**
     * Function necessary to store in memory data necessary to send the data.
     * @param playerConnection It is the class that contains the characteristics necessary for sending data.
     */
    public void savePlayer(PlayerConnection playerConnection) {
        if (playerConnection == null) {
            manageLogs.saveLog("WARN", "Attempted to save null PlayerConnection");
            return;
        }

        connections.put(playerConnection.getPlayerId(), playerConnection);
        manageLogs.saveLog("INFO", "Player connection saved (ID: " +
                playerConnection.getPlayerId() + ", IP: " +
                playerConnection.getIp().getHostAddress() + ":" +
                playerConnection.getPort() + ")");
    }

    /**
     * Function to close the UDP connection
     */
    public void closeClientUDP() {
        if (!socket.isClosed()) {
            socket.close();
            manageLogs.saveLog("INFO", "UDP socket closed successfully.");
        }
    }
}
