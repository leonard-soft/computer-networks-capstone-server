package org.example.encrypt;
import java.security.SecureRandom;
import java.util.Base64;

public class GenerateAES {

    private String key16 = "";
    private String key32 = "";

    /**
     * The function randomly generates keys of length 16 and 32 bits
     */
    public void createKeysRandom(){
        SecureRandom keysRandom = new SecureRandom();
        byte[] key128 = new byte[16];
        byte[] key256 = new byte[32];
        keysRandom.nextBytes(key128);
        keysRandom.nextBytes(key256);

        key16 = Base64.getEncoder().encodeToString(key128);
        key32 = Base64.getEncoder().encodeToString(key256);
    }

    public String getKey16(){
        return key16;
    }

    public String getKey32(){
        return key32;
    }
}
