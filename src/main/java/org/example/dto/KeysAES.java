package org.example.dto;

import java.net.InetAddress;

public class KeysAES {

    private String key;
    private String iv;

    public KeysAES(String key, String iv) {
        this.key = key;
        this.iv = iv;
    }

    public KeysAES() {
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getIv() {
        return iv;
    }

    public void setIv(String iv) {
        this.iv = iv;
    }
}
