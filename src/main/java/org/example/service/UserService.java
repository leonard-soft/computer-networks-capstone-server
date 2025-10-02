package org.example.service;

import javax.management.Query;

import org.example.dto.RequestPayload;
import org.example.hash.HashMethods;
import org.example.jpa.JpaUtil;

import jakarta.persistence.EntityManager;

public class UserService {
    
    /**
     * This method do the user register.
     * 
     * @param requestPayload data from the client
     */
    public void registerUser(RequestPayload requestPayload) {
        HashMethods hashMethods = new HashMethods();
        EntityManager entityManager = JpaUtil.getEntityManager();

        try {
            entityManager.getTransaction().begin();

            entityManager.createNativeQuery(
                "INSERT INTO game_user (username, password, user_state, user_match_win_quantity) " +
                "VALUES (?, ?, ?, ?)"
            )
            .setParameter(1, requestPayload.username)
            .setParameter(2, hashMethods.hash(requestPayload.Password))
            .setParameter(3, false) 
            .setParameter(4, 0)
            .executeUpdate();

            entityManager.getTransaction().commit();
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            e.printStackTrace();
        } finally {
            entityManager.close();
        }
    }
}
