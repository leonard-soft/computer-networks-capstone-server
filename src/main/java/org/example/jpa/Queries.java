package org.example.jpa;

import jakarta.persistence.TypedQuery;
import org.example.dto.GameDTO;
import org.example.dto.GameHasUser;
import org.example.entity.Player;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Query;
import org.example.logs.ManageLogs;

import java.util.List;

public class Queries {

    private final ManageLogs manageLogs = new ManageLogs();

    /**
     * This method is to do the query on the database to find the player by the id.
     *
     * @param username (username) of the client
     */
    public Player findPlayerByUsername(String username) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            manageLogs.saveLog("INFO", "Executing query to find player by username: " + username);
            Query query = em.createQuery("SELECT p FROM Player p WHERE p.username = :username", Player.class);
            query.setParameter("username", username);
            Player player = (Player) query.getSingleResult();
            manageLogs.saveLog("INFO", "Player found: " + username);
            return player;
        } catch (NoResultException e) {
            manageLogs.saveLog("WARN", "No player found with username: " + username);
            return null;
        } finally {
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
            manageLogs.saveLog("INFO", "New game created with ID: " + gameDTO.getGame_id());
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            manageLogs.saveLog("ERROR", "Error creating game: " + e.getMessage());
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
    public GameHasUser registerUserToGame(GameDTO game, Player user, boolean status) {
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
            manageLogs.saveLog("INFO", "User " + user.getUsername() + " registered to game " + game.getGame_id());
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            manageLogs.saveLog("ERROR", "Error registering user to game: " + e.getMessage());
            throw new RuntimeException("Error registering user to game: " + e.getMessage());
        } finally {
            entityManager.close();
        }
        return gameHasUser;
    }

    public GameHasUser findInvitation(int gameId, String username) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            manageLogs.saveLog("INFO", "Finding invitation for game " + gameId + " and user " + username);
            TypedQuery<GameHasUser> query = em.createQuery(
                    "SELECT ghu FROM GameHasUser ghu WHERE ghu.game_id.id = :gameId AND ghu.user_id.username = :username",
                    GameHasUser.class
            );
            query.setParameter("gameId", gameId);
            query.setParameter("username", username);
            return query.getSingleResult();
        } catch (NoResultException e) {
            manageLogs.saveLog("WARN", "No invitation found for game " + gameId + " and user " + username);
            return null;
        } finally {
            em.close();
        }
    }

    public void updateInvitation(GameHasUser invitation) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(invitation);
            em.getTransaction().commit();
            manageLogs.saveLog("INFO", "Invitation updated for game " + invitation.getGame_id().getGame_id());
        } catch (Exception e) {
            em.getTransaction().rollback();
            manageLogs.saveLog("ERROR", "Error updating invitation: " + e.getMessage());
            throw new RuntimeException("Error updating invitation: " + e.getMessage());
        } finally {
            em.close();
        }
    }

    public void deleteInvitation(GameHasUser invitation) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.remove(em.contains(invitation) ? invitation : em.merge(invitation));
            em.getTransaction().commit();
            manageLogs.saveLog("INFO", "Invitation deleted for game " + invitation.getGame_id().getGame_id());
        } catch (Exception e) {
            em.getTransaction().rollback();
            manageLogs.saveLog("ERROR", "Error deleting invitation: " + e.getMessage());
            throw new RuntimeException("Error deleting invitation: " + e.getMessage());
        } finally {
            em.close();
        }
    }

    public GameDTO findGameById(int gameId) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            manageLogs.saveLog("INFO", "Finding game by ID: " + gameId);
            return em.find(GameDTO.class, gameId);
        } finally {
            em.close();
        }
    }

    public List<GameDTO> findActiveGames() {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            manageLogs.saveLog("INFO", "Fetching active games");
            TypedQuery<GameDTO> query = em.createQuery(
                    "SELECT g FROM GameDTO g WHERE g.game_status = true", GameDTO.class
            );
            List<GameDTO> games = query.getResultList();
            manageLogs.saveLog("INFO", "Found " + games.size() + " active games");
            return games;
        } catch (Exception e) {
            manageLogs.saveLog("ERROR", "Error fetching active games: " + e.getMessage());
            throw new RuntimeException("Error fetching active games: " + e.getMessage());
        } finally {
            em.close();
        }
    }

    public void updateGameStatus(int gameId, boolean status) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            GameDTO game = em.find(GameDTO.class, gameId);
            if (game != null) {
                game.setGame_status(status);
                em.merge(game);
                em.getTransaction().commit();
                manageLogs.saveLog("INFO", "Game status updated for game " + gameId + " to " + status);
            } else {
                manageLogs.saveLog("WARN", "Game not found for status update: " + gameId);
            }
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            manageLogs.saveLog("ERROR", "Error updating game status: " + e.getMessage());
            throw new RuntimeException("Error updating game status: " + e.getMessage());
        } finally {
            em.close();
        }
    }

    public List<String> getOnlineUsers() {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            manageLogs.saveLog("INFO", "Fetching online users");
            TypedQuery<String> query = em.createQuery(
                    "SELECT p.username FROM Player p WHERE p.userState = true", String.class
            );
            List<String> users = query.getResultList();
            manageLogs.saveLog("INFO", "Found " + users.size() + " online users");
            return users;
        } catch (Exception e) {
            manageLogs.saveLog("ERROR", "Error fetching online users: " + e.getMessage());
            throw new RuntimeException("Error fetching online users: " + e.getMessage());
        } finally {
            em.close();
        }
    }
}
