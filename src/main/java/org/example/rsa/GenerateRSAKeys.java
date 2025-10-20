package org.example.rsa;

import org.example.logs.ManageLogs;

import javax.crypto.Cipher;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class GenerateRSAKeys {
    private final ManageLogs logs;
    private PrivateKey privateKey;
    private PublicKey publicKey;

    private static final String PRIVATE_KEY_FILE = "server_private.key";
    private static final String PUBLIC_KEY_FILE = "server_public.key";

    public GenerateRSAKeys() {
        logs = new ManageLogs();
    }

    public void initializeKeys() {
        try {
            File privateFile = new File(PRIVATE_KEY_FILE);
            File publicFile = new File(PUBLIC_KEY_FILE);

            if (privateFile.exists() && publicFile.exists()) {
                byte[] privateBytes = Base64.getDecoder().decode(Files.readAllBytes(privateFile.toPath()));
                byte[] publicBytes = Base64.getDecoder().decode(Files.readAllBytes(publicFile.toPath()));

                KeyFactory factory = KeyFactory.getInstance("RSA");
                privateKey = factory.generatePrivate(new PKCS8EncodedKeySpec(privateBytes));
                publicKey = factory.generatePublic(new X509EncodedKeySpec(publicBytes));
            } else {
                KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
                generator.initialize(2048);
                KeyPair pair = generator.generateKeyPair();

                privateKey = pair.getPrivate();
                publicKey = pair.getPublic();

                saveKeyToFile(PRIVATE_KEY_FILE, privateKey.getEncoded());
                saveKeyToFile(PUBLIC_KEY_FILE, publicKey.getEncoded());
            }

        } catch (Exception e) {
            logs.saveLog("ERROR", "RSA key initialization failed: " + e.getMessage());
        }
    }

    private void saveKeyToFile(String filename, byte[] keyBytes) {
        try (FileOutputStream fos = new FileOutputStream(filename)) {
            fos.write(Base64.getEncoder().encode(keyBytes));
        } catch (Exception e) {
            logs.saveLog("ERROR", "Failed to save key " + filename + ": " + e.getMessage());
        }
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public String encryptData(String data, PublicKey clientPublicKey){
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, clientPublicKey);
            byte[] encryptedBytes = cipher.doFinal(data.getBytes("UTF-8"));
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            logs.saveLog("ERROR", "RSA encryption failed: " + e.getMessage());
            return null;
        }
    }

    public String decrypt(String encryptedData){
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedData));
            return new String(decryptedBytes, "UTF-8");
        } catch (Exception e) {
            logs.saveLog("ERROR", "RSA decryption failed: " + e.getMessage());
            return null;
        }
    }

    public PublicKey convertPublicKeyClient(String publicKeyBase64) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(publicKeyBase64);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");

        return keyFactory.generatePublic(keySpec);
    }
}
