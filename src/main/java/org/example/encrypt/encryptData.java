package org.example.encrypt;

import org.example.logs.ManageLogs;
import org.example.service.RoomService;
import org.example.dto.KeysAES;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.net.InetAddress;
import java.util.Base64;

public class encryptData {

    private ManageLogs logs;
    private RoomService service;

    public encryptData(RoomService service, ManageLogs logs) {
        this.service = service;
        this.logs = logs;
    }

    public encryptData() {

    }

    private Cipher initCipher(int mode, InetAddress infoClient) throws Exception {
        KeysAES keys = service.findKeys(infoClient);
        byte[] keyBytes = Base64.getDecoder().decode(keys.getKey());
        byte[] ivBytes = Base64.getDecoder().decode(keys.getIv());
        SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(mode, keySpec, ivSpec);
        return cipher;
    }

    public String encrypt(String data, InetAddress infoClient) {
        try {
            Cipher cipher = initCipher(Cipher.ENCRYPT_MODE, infoClient);
            byte[] encrypted = cipher.doFinal(data.getBytes("UTF-8"));
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            logs.saveLog("ERROR", "AES encrypt error: " + e.getMessage());
            return null;
        }
    }

    public String decrypt(String encryptedBase64, InetAddress infoClient) {
        try {
            Cipher cipher = initCipher(Cipher.DECRYPT_MODE, infoClient);
            byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(encryptedBase64));
            return new String(decrypted, "UTF-8");
        } catch (Exception e) {
            logs.saveLog("ERROR", "AES decrypt error: " + e.getMessage());
            return null;
        }
    }
}
