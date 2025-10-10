package org.example.jpa;

import java.util.List;

import org.example.entity.Player;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;

public class Queries {

    /**
     * This method is to do the query on the database to find the player by the id.
     *
     * @param username (username) of the client
     */
    public Player findPlayerByUsername(String username){
        EntityManager em = JpaUtil.getEntityManager();
        try {
            Query query = em.createQuery("SELECT p FROM Player p WHERE p.username = :username", Player.class);
            query.setParameter("username", username);
            return (Player) query.getSingleResult();
        }catch (NoResultException e){
            return null;
        }finally {
            em.close();
        }
    }

    public List<String> getOnlineUsers(){
        EntityManager em = JpaUtil.getEntityManager();
        try {
            TypedQuery<String> query = em.createQuery(
                "SELECT p.username FROM Player p WHERE p.userState = true", String.class
            );

            return query.getResultList();
        }catch(Exception e){
            throw new RuntimeException("Error fetiching online users: " + e.getMessage());
        }finally{
            em.close();
        }
    }
}
