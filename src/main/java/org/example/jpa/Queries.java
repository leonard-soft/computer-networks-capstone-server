package org.example.jpa;

import jakarta.persistence.TypedQuery;
import org.example.dto.GameDTO;
import org.example.dto.GameHasUser;
import org.example.dto.PlayerDTO;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Query;

import java.util.List;

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

    /**
     * This method is a query to create a new game in the database to which the object
     * saved in the database is returned.
     *
     * @return GameDTO
     */
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

    /**
     * This method is a query to register a user to a game in the database to which it
     * returns the object saved in the database
     *
     * @param game previously created game
     * @param user user or client to register in the game
     * @return GameHasUser
     */
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
