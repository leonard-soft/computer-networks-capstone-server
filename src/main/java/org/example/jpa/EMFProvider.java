package org.example.jpa;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.HashMap;
import java.util.Map;

public class EMFProvider {

    /**
     * Basically this method return a database connection,
     * similar than javascript and python and another languages.
     * 
     * @return EntityManagerFactory object
     */
    public static EntityManagerFactory createEFM() {
        Map<String, String> config = new HashMap<>();

        String dbUrl = System.getenv("DBURL");
        String dbUser = System.getenv("DBUSER");
        String dbPass = System.getenv("DBPASS");

        System.out.println("DBURL: " + dbUrl);
        System.out.println("DBUSER: " + dbUser);
        System.out.println("DBPASS: " + dbPass);

        config.put("jakarta.persistence.jdbc.driver", "org.postgresql.Driver");
        config.put("jakarta.persistence.jdbc.url", dbUrl);
        config.put("jakarta.persistence.jdbc.user", dbUser);
        config.put("jakarta.persistence.jdbc.password", dbPass);

        return Persistence.createEntityManagerFactory("my-persistence-unit", config);
    }
}
