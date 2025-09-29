package org.example.jpa;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.HashMap;
import java.util.Map;

public class EMFProvider {
    public static EntityManagerFactory createEFM() {
        Map<String, String> config = new HashMap<>();

        config.put("jakarta.persistence.jdbc.driver", "org.postgresql.Driver");
        config.put("jakarta.persistence.jdbc.url", System.getenv("DBURL"));
        config.put("jakarta.persistence.jdbc.user", System.getenv("DBUSER"));
        config.put("jakarta.persistence.jdbc.password", System.getenv("DBPASS"));

        return Persistence.createEntityManagerFactory("my-persistence-unit", config);
    }
}
