package org.example.service;

import java.util.ArrayList;
import java.util.List;

import org.example.dto.ranking.RankUserDto;
import org.example.jpa.JpaUtil;
import org.example.logs.ManageLogs;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

public class RankService {

    private ManageLogs manageLogs = new ManageLogs();

    public RankService() {}

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
                "SELECT username, user_match_win_quantity FROM game_user ORDER BY user_match_win_quantity ASC"
            ).getResultList();

            for (Object[] row : results) {
                users.add(new RankUserDto(
                    (String) row[0],
                    ((Number) row[1]).intValue()
                ));
            }

            transaction.commit();

        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) transaction.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }

        return users;
    }

}
