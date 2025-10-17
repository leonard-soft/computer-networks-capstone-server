package org.example.dto;

import java.util.Map;

public class GameSession {

    private int gameId;
    private int player1Id;
    private int player2Id;
    private int player1Health = 100;
    private int player2Health = 100;

    // We can store positions here if the server needs to be authoritative about them
    // For now, we'll focus on health management

    public GameSession(int gameId, int player1Id, int player2Id) {
        this.gameId = gameId;
        this.player1Id = player1Id;
        this.player2Id = player2Id;
    }

    public int getGameId() {
        return gameId;
    }

    public int getPlayer1Id() {
        return player1Id;
    }

    public int getPlayer2Id() {
        return player2Id;
    }

    public int getPlayer1Health() {
        return player1Health;
    }

    public void setPlayer1Health(int player1Health) {
        this.player1Health = player1Health;
    }

    public int getPlayer2Health() {
        return player2Health;
    }

    public void setPlayer2Health(int player2Health) {
        this.player2Health = player2Health;
    }

    /**
     * Applies damage to a player and returns true if the game is over.
     * @param targetPlayerId The ID of the player taking damage.
     * @param damageAmount The amount of damage to apply.
     * @return true if a player's health is at or below 0, false otherwise.
     */
    public boolean applyDamage(int targetPlayerId, int damageAmount) {
        if (targetPlayerId == player1Id) {
            this.player1Health -= damageAmount;
            return this.player1Health <= 0;
        } else if (targetPlayerId == player2Id) {
            this.player2Health -= damageAmount;
            return this.player2Health <= 0;
        }
        return false;
    }
}
