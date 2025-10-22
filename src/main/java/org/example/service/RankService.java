package org.example.service;

import java.util.ArrayList;
import java.util.List;

import org.example.dto.ranking.RankUserDto;
import org.example.entity.Player;
import org.example.jpa.JpaUtil;
import org.example.logs.ManageLogs;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;

public class RankService {

    private ManageLogs manageLogs = new ManageLogs();

    public RankService() {
    }

    /**
     * this method get the ranking of users from the
     * database.
     * 
     * @return users list
     */
    public List<RankUserDto> getUserMatchRanking() {
        EntityManager em = JpaUtil.getEntityManager();
        EntityTransaction transaction = null;
        List<RankUserDto> users = new ArrayList<>();

        try {
            transaction = em.getTransaction();
            transaction.begin();

            List<Object[]> results = em.createNativeQuery(
                    "SELECT username, user_match_win_quantity FROM game_user ORDER BY user_match_win_quantity ASC")
                    .getResultList();

            for (Object[] row : results) {
                users.add(new RankUserDto(
                        (String) row[0],
                        ((Number) row[1]).intValue()));
            }

            transaction.commit();

        } catch (Exception e) {
            if (transaction != null && transaction.isActive())
                transaction.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }

        return users;
    }

    public void updateUserVictories(int winnerId) {
        EntityManager em = JpaUtil.getEntityManager();
        EntityTransaction transaction = null;

        try {
            transaction = em.getTransaction();
            transaction.begin();

            Player winner;
            try {
                winner = (Player) em.createNativeQuery(
                        "SELECT * FROM game_user WHERE user_id = :id",
                        Player.class)
                        .setParameter("id", winnerId)
                        .getSingleResult();
            } catch (NoResultException e) {
                System.out.println("No se encontró el jugador con id " + winnerId);
                transaction.rollback();
                return;
            }

            int winQuantityUpdated = winner.getUserMatchWinCount() + 1;

            em.createNativeQuery(
                    "UPDATE game_user SET user_match_win_quantity = :update_quantity WHERE user_id = :id")
                    .setParameter("update_quantity", winQuantityUpdated)
                    .setParameter("id", winnerId)
                    .executeUpdate();

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null && transaction.isActive())
                transaction.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

}
