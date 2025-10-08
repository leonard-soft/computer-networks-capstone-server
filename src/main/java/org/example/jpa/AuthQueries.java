package org.example.jpa;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Query;
import org.example.dto.PlayerDTO;

public class AuthQueries {

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

}
