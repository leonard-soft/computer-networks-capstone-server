package org.example.jpa;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;

public class AuthQueries {

    public void findPlayerByUsername(String username){
        EntityManager em = JpaUtil.getEntityManager();
        Query query = em.createQuery("SELECT P from player P WHERE P.username = :username");
        query.setParameter("username", username);
    }

}
