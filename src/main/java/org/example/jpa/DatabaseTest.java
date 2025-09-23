package org.example.jpa;

import org.example.jpa.JpaUtil;
import jakarta.persistence.EntityManager;

public class DatabaseTest {

    private JpaUtil jpaUtil;

    public void databaseTest() {
        try {

            /* creating the entity manager */
            EntityManager em = jpaUtil.getEntityManager();

            /* this is printed if the connection is successfull */
            System.out.println("Connection completed successfully");

            /* closing the database connection */
            em.close();
        } catch (Exception e) {
            System.err.println("Connection error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            JpaUtil.close();
        }
    }

}
