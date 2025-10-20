package org.example.dto;

import java.net.InetAddress;

public class Keys {

    private InetAddress infoClient;
    private String publicServerKey;
    private String publicUserKey;

    public Keys(InetAddress infoClient, String publicServerKey, String publicUserKey) {
        this.infoClient = infoClient;
        this.publicServerKey = publicServerKey;
        this.publicUserKey = publicUserKey;
    }

    public Keys() {
    }

    public InetAddress getInfoClient() {
        return infoClient;
    }

    public void setInfoClient(InetAddress infoClient) {
        this.infoClient = infoClient;
    }

    public String getPublicServerKey() {
        return publicServerKey;
    }

    public void setPublicServerKey(String publicServerKey) {
        this.publicServerKey = publicServerKey;
    }

    public String getPublicUserKey() {
        return publicUserKey;
    }

    public void setPublicUserKey(String publicUserKey) {
        this.publicUserKey = publicUserKey;
    }
}
