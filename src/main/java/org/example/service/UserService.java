package org.example.service;

import org.example.dto.RequestPayload;
import org.example.entity.Player;
import org.example.hash.HashMethods;
import org.example.jpa.JpaUtil;
import org.example.jpa.Queries;
import org.example.logs.ManageLogs;

import jakarta.persistence.EntityManager;

import java.util.List;

public class UserService {

    private final ManageLogs manageLogs = new ManageLogs();

    /**
     * This method do the user register.
     *
     * @param requestPayload data from the client
     */
    public void registerUser(RequestPayload requestPayload) {
        HashMethods hashMethods = new HashMethods();
        EntityManager entityManager = JpaUtil.getEntityManager();

        try {
            manageLogs.saveLog("INFO", "Attempting to register user: " + requestPayload.username);
            entityManager.getTransaction().begin();

            entityManager.createNativeQuery(
                            "INSERT INTO game_user (username, password, user_state, user_match_win_quantity) " +
                                    "VALUES (?, ?, ?, ?)"
                    )
                    .setParameter(1, requestPayload.username)
                    .setParameter(2, hashMethods.hash(requestPayload.password))
                    .setParameter(3, false)
                    .setParameter(4, 0)
                    .executeUpdate();

            entityManager.getTransaction().commit();
            manageLogs.saveLog("INFO", "User registered successfully: " + requestPayload.username);
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            manageLogs.saveLog("ERROR", "Error registering user " + requestPayload.username + ": " + e.getMessage());
            e.printStackTrace();
        } finally {
            entityManager.close();
        }
    }

    /**
     * This method returns true if the credentials are correct, and false if not.
     *
     * @param requestPayload data from the user
     */
    public boolean loginUser(RequestPayload requestPayload) {
        HashMethods hashMethods = new HashMethods();
        Queries queries = new Queries();

        manageLogs.saveLog("INFO", "Login attempt for user: " + requestPayload.username);
        Player player = queries.findPlayerByUsername(requestPayload.username);
        if (player == null) {
            manageLogs.saveLog("WARN", "Login failed: User not found - " + requestPayload.username);
            return false;
        }

        boolean success = hashMethods.compareHash(requestPayload.password, player.getPassword());
        if (success) {
            manageLogs.saveLog("INFO", "Login successful for user: " + requestPayload.username);
        } else {
            manageLogs.saveLog("WARN", "Login failed: Invalid credentials for user - " + requestPayload.username);
        }
        return success;
    }

    /**
     * This method update user state
     * @param  username, boolean state
     *
     */
    public void updateUserState(String username, boolean state) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            Player player = em.find(Player.class, username);
            if (player != null) {
                player.setUserState(state);
                em.getTransaction().begin();
                em.merge(player);
                em.getTransaction().commit();
                manageLogs.saveLog("INFO", "User state updated for " + username + " to " + (state ? "online" : "offline"));
            }
        } catch (Exception e) {
            em.getTransaction().rollback();
            manageLogs.saveLog("ERROR", "Error updating user state for " + username + ": " + e.getMessage());
            throw new RuntimeException("Error updating user state: " + e.getMessage());
        }
    }

    /**
     * This method returns all the online users on the DB
     */
    public List<String> getOnlineUsers() {
        Queries queries = new Queries();
        return queries.getOnlineUsers();
    }
}
