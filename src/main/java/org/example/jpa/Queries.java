package org.example.jpa;

import org.example.dto.PlayerDTO;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Query;

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

    public GameDTO createNewGame() {
        EntityManager entityManager = JpaUtil.getEntityManager();
        GameDTO gameDTO = null;
        try {
            entityManager.getTransaction().begin();
            GameDTO game = new GameDTO();
            game.setGame_status(false);
            entityManager.persist(game);
            entityManager.getTransaction().commit();
            gameDTO = game;

        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            e.printStackTrace();
        } finally {
            entityManager.close();
        }

        return gameDTO;
    }

    public GameHasUser registerUserToGame(GameDTO game, PlayerDTO user){
        EntityManager entityManager = JpaUtil.getEntityManager();
        GameHasUser gameHasUser = null;
        try {
            entityManager.getTransaction().begin();
            GameHasUser gameRegister = new GameHasUser();
            gameRegister.setGame_id(game);
            gameRegister.setUser_id(user);
            gameRegister.setUsers_status(true);
            entityManager.persist(gameRegister);
            entityManager.getTransaction().commit();
            gameHasUser = gameRegister;
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            e.printStackTrace();
        } finally {
            entityManager.close();
        }
        return gameHasUser;
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
