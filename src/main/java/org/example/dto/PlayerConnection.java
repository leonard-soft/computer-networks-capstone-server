package org.example.dto;

import java.net.InetAddress;
import java.util.Objects;

public class PlayerConnection {

    private final int playerId;
    private final InetAddress ip;
    private final int port;
    private boolean aesKeysSent; // Flag para saber si ya se enviaron las llaves AES

    public PlayerConnection(int playerId, InetAddress ip, int port) {
        this.playerId = playerId;
        this.ip = ip;
        this.port = port;
        this.aesKeysSent = false;
    }

    public int getPlayerId() {
        return playerId;
    }

    public InetAddress getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    public boolean isAesKeysSent() {
        return aesKeysSent;
    }

    public void setAesKeysSent(boolean aesKeysSent) {
        this.aesKeysSent = aesKeysSent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlayerConnection that = (PlayerConnection) o;
        return playerId == that.playerId &&
                port == that.port &&
                Objects.equals(ip, that.ip);
    }

    @Override
    public int hashCode() {
        return Objects.hash(playerId, ip, port);
    }

    @Override
    public String toString() {
        return "PlayerConnection{" +
                "playerId=" + playerId +
                ", ip=" + ip.getHostAddress() +
                ", port=" + port +
                ", aesKeysSent=" + aesKeysSent +
                '}';
    }
}
