package org.example.hash;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;

import java.io.InputStream;
import java.util.Properties;

public class HashMethods {

    private final String pepper;

    public HashMethods() {
        this.pepper = loadPepper();
    }

    public String hash(String data) {
        Argon2 argon2 = Argon2Factory.create();
        char[] passwordChars = (data + pepper).toCharArray();
        try {
            return argon2.hash(4, 65536, 1, passwordChars);
        } finally {
            argon2.wipeArray(passwordChars);
        }
    }

    public boolean compareHash(String inputData, String storedHash) {
        Argon2 argon2 = Argon2Factory.create();
        char[] passwordChars = (inputData + pepper).toCharArray();
        try {
            return argon2.verify(storedHash, passwordChars);
        } finally {
            argon2.wipeArray(passwordChars);
        }
    }

    private String loadPepper() {
        Properties proc = new Properties();
        try (InputStream input = HashMethods.class.getClassLoader().getResourceAsStream("hash.properties")) {
            if (input == null) throw new RuntimeException("hash.properties not found");
            proc.load(input);
            return proc.getProperty("PEPPER");
        } catch (Exception e) {
            throw new RuntimeException("Failed to load pepper", e);
        }
    }
}
