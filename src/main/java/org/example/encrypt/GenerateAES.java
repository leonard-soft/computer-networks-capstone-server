package org.example.encrypt;

import org.example.logs.ManageLogs;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.security.SecureRandom;
import java.util.Base64;

public class GenerateAES {

    private final ManageLogs logs;
    private static final String KEY_FILE = "server_key_aes.key";
    private static final String IV_FILE = "server_iv_aes.key";

    public GenerateAES() {
        this.logs = new ManageLogs();
    }

    /**
     * Generates random AES keys (256-bit key and 128-bit IV)
     */
    public void createKeysRandom() {
        try {
            File keyFile = new File(KEY_FILE);
            File ivFile = new File(IV_FILE);

            if (!keyFile.exists() || !ivFile.exists()) {
                SecureRandom random = new SecureRandom();
                byte[] key256 = new byte[32];
                byte[] iv128 = new byte[16];

                random.nextBytes(key256);
                random.nextBytes(iv128);

                createFile(KEY_FILE, key256);
                createFile(IV_FILE, iv128);
            }
        } catch (Exception e) {
            logs.saveLog("ERROR", "AES key initialization failed: " + e.getMessage());
        }
    }

    private void createFile(String fileName, byte[] value) {
        try (FileOutputStream fos = new FileOutputStream(fileName)) {
            fos.write(Base64.getEncoder().encode(value));
        } catch (Exception e) {
            logs.saveLog("ERROR", "Failed to save key " + fileName + ": " + e.getMessage());
        }
    }

    public String getIv() {
        try {
            return Files.readString(new File(IV_FILE).toPath());
        } catch (Exception e) {
            logs.saveLog("ERROR", "Error reading IV file: " + e.getMessage());
            return "";
        }
    }

    public String getKey() {
        try {
            return Files.readString(new File(KEY_FILE).toPath());
        } catch (Exception e) {
            logs.saveLog("ERROR", "Error reading key file: " + e.getMessage());
            return "";
        }
    }
}
