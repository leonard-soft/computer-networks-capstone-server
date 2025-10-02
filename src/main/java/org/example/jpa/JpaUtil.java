package org.example.jpa;

import org.example.jpa.EMFProvider;

import jakarta.persistence.EntityManager;  /* Entity manager lib */
import jakarta.persistence.EntityManagerFactory; /* Entity Factory lib */


public class JpaUtil {

    /**
     * this is the entity manager factory, with this you 
     * have access to the database connection with java.
     */
    private static final EntityManagerFactory EntityManagerFactory;

    static {
        try {
           /* this is the entityCreationFactory manager  */ 
           EntityManagerFactory = EMFProvider.createEFM();
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
        return EntityManagerFactory.createEntityManager();
    }

    /**
     * this method close the database connection using
     * the entity manager.
     */
    public static void close() {
        if (EntityManagerFactory != null && EntityManagerFactory.isOpen()) {
            EntityManagerFactory.close();
        }
    }

}
