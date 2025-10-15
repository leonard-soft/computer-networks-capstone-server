package org.example.jpa;

import jakarta.persistence.TypedQuery;
import org.example.dto.GameDTO;
import org.example.dto.GameHasUser;
import java.util.List;

import org.example.entity.Player;

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


    /**
     * This method is a query to create a new game in the database to which the object
     * saved in the database is returned.
     *
     * @return GameDTO
     */
    public GameDTO registerNewGame() {
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
            throw new RuntimeException("Error creating game: " + e.getMessage());
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
    public GameHasUser registerUserToGame(GameDTO game, Player user, boolean status){
        EntityManager entityManager = JpaUtil.getEntityManager();
        GameHasUser gameHasUser = null;
        try {
            entityManager.getTransaction().begin();
            GameHasUser gameRegister = new GameHasUser();
            gameRegister.setGame_id(game);
            gameRegister.setUser_id(user);
            gameRegister.setUsers_status(status);
            entityManager.persist(gameRegister);
            entityManager.getTransaction().commit();
            gameHasUser = gameRegister;
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            throw new RuntimeException("Error registering user to game: " + e.getMessage());
        } finally {
            entityManager.close();
        }
        return gameHasUser;
    }

    public GameHasUser findInvitation(int gameId, String username) {
        EntityManager em = JpaUtil.getEntityManager();
        try{
            TypedQuery<GameHasUser> query = em.createQuery(
                    "SELECT ghu FROM GameHasUser ghu WHERE ghu.game_id.id = :gameId AND ghu.user_id.username = :username",
                    GameHasUser.class
            );
            query.setParameter("gameId", gameId);
            query.setParameter("username", username);
            return  query.getSingleResult();
        }catch (NoResultException e){
            return null;
        }finally {
            em.close();
        }
    }

    public void updateInvitation(GameHasUser invitation){
        EntityManager em = JpaUtil.getEntityManager();
        try{
            em.getTransaction().begin();
            em.merge(invitation);
            em.getTransaction().commit();
        }catch (Exception e){
            em.getTransaction().rollback();
            throw new RuntimeException("Error updating invitation: " + e.getMessage());
        }finally {
            em.close();
        }
    }

    public void deleteInvitation(GameHasUser invitation){
        EntityManager em = JpaUtil.getEntityManager();
        try{
            em.getTransaction().begin();
            em.remove(em.contains(invitation) ? invitation : em.merge(invitation));
            em.getTransaction().commit();
        }catch (Exception e){
            em.getTransaction().rollback();
            throw new RuntimeException("Error deleting invitation: " + e.getMessage());
        }finally {
            em.close();
        }
    }

    public GameDTO findGameById(int gameId){
        EntityManager em = JpaUtil.getEntityManager();
        try {
            return em.find(GameDTO.class, gameId);
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
