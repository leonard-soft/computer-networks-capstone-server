package org.example.jpa;

import jakarta.persistence.EntityManager;  /* Entity manager lib */
import jakarta.persistence.EntityManagerFactory; /* Entity Factory lib */
import jakarta.persistence.Persistence; /* lib to have access to database */

public class JpaUtil {

    /**
     * this is the entity manager factory, with this you 
     * have access to the database connection with java.
     */
    private static final EntityManagerFactory entityManager;

    static {
        try {
           /* this is the entityCreationFactory manager  */ 
           entityManager = Persistence.createEntityManagerFactory("my-persistence-unit");
        } catch (Exception e) {
            System.err.println("EntityManagerFactory: " + e.getMessage());
            throw new ExceptionInInitializerError(e);
        }
    }

    /**
     * this method provide the database connection into the
     * object, basically open the database connection to do 
     * operations in database.
     * 
     * @return Entity Manager Object
     */
    public static EntityManager getEntityManager() {
        return entityManager.createEntityManager();
    }

    /**
     * this method close the database connection using
     * the entity manager.
     */
    public static void close() {
        if (entityManager != null && entityManager.isOpen()) {
            entityManager.close();
        }
    }

}
