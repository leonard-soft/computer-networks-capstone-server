package org.example.dto;

import java.net.InetAddress;

public class PlayerConnection {

    private int playerId;
    private InetAddress ip;
    private int port;

    public PlayerConnection(int playerId, InetAddress ip, int port) {
        this.playerId = playerId;
        this.ip = ip;
        this.port = port;
    }

    public PlayerConnection() {
    }

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public InetAddress getIp() {
        return ip;
    }

    public void setIp(InetAddress ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
