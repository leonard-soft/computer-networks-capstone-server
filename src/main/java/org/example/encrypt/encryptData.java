package org.example.encrypt;

import org.example.logs.ManageLogs;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class encryptData {

    private final ManageLogs logs;
    private final GenerateAES generateAES;

    public encryptData() {
        this.logs = new ManageLogs();
        this.generateAES = new GenerateAES();
    }


    private Cipher initCipher(int mode) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(generateAES.getKey());
        byte[] ivBytes = Base64.getDecoder().decode(generateAES.getIv());
        SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(mode, keySpec, ivSpec);
        return cipher;
    }

    public String encrypt(String data) {
        try {
            Cipher cipher = initCipher(Cipher.ENCRYPT_MODE);
            byte[] encrypted = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            logs.saveLog("ERROR", "AES encrypt error: " + e.getMessage());
            return null;
        }
    }

    public String decrypt(String encryptedBase64) {
        try {
            Cipher cipher = initCipher(Cipher.DECRYPT_MODE);
            byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(encryptedBase64));
            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (Exception e) {
            logs.saveLog("ERROR", "AES decrypt error: " + e.getMessage());
            return null;
        }
    }
}
