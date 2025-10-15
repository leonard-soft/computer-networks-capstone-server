package org.example.udp;

import org.example.dto.DataTransferDTO;
import org.example.dto.PlayerConnection;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;

public class UdpService {

    private final DatagramSocket socket;
    private Map<Integer, PlayerConnection> connections = new HashMap<>();

    public UdpService() throws SocketException {
        this.socket = new DatagramSocket();
    }

    public void clientUdp(DataTransferDTO data){
        PlayerConnection player = connections.get(data.getIdPlayer());
        if(player == null) throw new RuntimeException("No connection found for player with ID: "+data.getIdPlayer());
        try {
            byte[] buffer = objectToBytes(data);
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, player.getIp(), player.getPort());
            socket.send(packet);
        }catch (IOException e){
            throw new RuntimeException(e.getMessage());
        }
    }

    private byte[] objectToBytes(Object object){
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             ObjectOutputStream objectOutputStream  = new ObjectOutputStream(outputStream)){
            objectOutputStream.writeObject(object);
            return outputStream.toByteArray();
        }catch (IOException e){
            throw new RuntimeException(e.getMessage());
        }
    }

    public void safePlayer(PlayerConnection playerConnection){
        int idConnection = connections.size() + 1;
        connections.put(idConnection, playerConnection);
    }

    public void closeClientUDP(){
        if (!socket.isClosed()){
            socket.close();
        }
    }
}
