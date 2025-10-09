package org.example.jpa;

import java.util.List;

import org.example.dto.PlayerDTO;

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
    public PlayerDTO findPlayerByUsername(String username){
        EntityManager em = JpaUtil.getEntityManager();
        try {
            Query query = em.createQuery("SELECT p FROM PlayerDTO p WHERE p.username = :username", PlayerDTO.class);
            query.setParameter("username", username);
            return (PlayerDTO) query.getSingleResult();
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
                "SELECT p.username FROM PlayerDTO p WHERE p.userState = true", String.class
            );

            return query.getResultList();
        }catch(Exception e){
            throw new RuntimeException("Error fetiching online users: " + e.getMessage());
        }finally{
            em.close();
        }
    }
}
