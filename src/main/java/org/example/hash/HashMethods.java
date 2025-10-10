package org.example.hash;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;

import java.io.InputStream;
import java.util.Properties;

public class HashMethods {

    /**
     * Variable that stores the pepper
     */
    private final String pepper;

    /**
     * Constructor where a value is assigned to the variable pepper
     */
    public HashMethods() {
        this.pepper = loadPepper();
    }

    /**
     * Function to hash the data passed in the parameters
     * @param data to hash
     * @return a String which is already the hash
     */
    public String hash(String data) {
        Argon2 argon2 = Argon2Factory.create();
        char[] passwordChars = (data + pepper).toCharArray();
        try {
            return argon2.hash(4, 65536, 1, passwordChars);
        } finally {
            argon2.wipeArray(passwordChars);
        }
    }

    /**
     * Function that compares a hash with data in text format
     * @param inputData plain text data
     * @param storedHash hashed data that is stored in the database
     * @return true if the data to be compared are equal or false if they are not equal
     */
    public boolean compareHash(String inputData, String storedHash) {
        Argon2 argon2 = Argon2Factory.create();
        char[] passwordChars = (inputData + pepper).toCharArray();
        try {
            return argon2.verify(storedHash, passwordChars);
        } finally {
            argon2.wipeArray(passwordChars);
        }
    }

    /**
     *Function to load the contents of the hash.properties file
     * @return the content found inside the hash.properties file
     */
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
