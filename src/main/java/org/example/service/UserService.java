package org.example.service;

import org.example.dto.RequestPayload;
import org.example.entity.Player;
import org.example.hash.HashMethods;
import org.example.jpa.JpaUtil;
import org.example.jpa.Queries;
import org.example.logs.ManageLogs;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Query;

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
    public void updateUserState(String username, String state) {
        EntityManager em = JpaUtil.getEntityManager();
        EntityTransaction transaction = null;

        try { 
            transaction = em.getTransaction();
            transaction.begin();
        
            Long userCount = (Long) em.createNativeQuery("SELECT COUNT(*) FROM game_user WHERE username = :username")
            .setParameter("username", username)
            .getSingleResult();
            System.out.println("DEBUG - Users found: " + userCount + " for username: " + username);

            Query query = em.createNativeQuery("UPDATE game_user SET user_state = :state WHERE username = :username");
            query.setParameter("state", state);
            query.setParameter("username", username);

            int updatedRows = query.executeUpdate();
            em.getTransaction().commit();
        
            if (updatedRows > 0) {
                String status = "true".equalsIgnoreCase(state) ? "online" : "offline";
                manageLogs.saveLog("INFO", "User state updated for " + username + " to " + status);
            } else {
                manageLogs.saveLog("WARN", "No user found with username: " + username);
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

    /**
     * this method is to obtein the player using the 
     * username.
     * 
     * @param username string value
     * @return player Entity
     */
    public Player getByUsername(String username) {
        EntityManager em = JpaUtil.getEntityManager();
    
        try {
            List<Player> result = em.createQuery(
                    "SELECT p FROM Player p WHERE p.username = :username", Player.class)
                    .setParameter("username", username)
                    .getResultList();
    
            return result.isEmpty() ? null : result.get(0);
    
        } catch (Exception e) {
            manageLogs.saveLog("ERROR", "Error searching the player " + username + ": " + e.getMessage());
            throw new RuntimeException("Error searching player: " + e.getMessage());
        } finally {
            em.close(); 
        }
    }
    
}
