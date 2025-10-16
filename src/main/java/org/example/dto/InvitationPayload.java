package org.example.dto;

/**
 * Represents the payload for sending, accepting, or denying a game invitation.
 * This DTO carries the essential information to identify the involved players and the game.
 */
public class InvitationPayload {
    private String inviterUsername;
    private String invitedUsername;
    private int gameId;

    /**
     * Constructs a new InvitationPayload.
     *
     * @param inviterUsername The username of the player sending the invitation.
     * @param invitedUsername The username of the player being invited.
     * @param gameId          The ID of the game for the invitation.
     */
    public InvitationPayload(String inviterUsername, String invitedUsername, int gameId) {
        this.inviterUsername = inviterUsername;
        this.invitedUsername = invitedUsername;
        this.gameId = gameId;
    }

    /**
     * Gets the username of the player who sent the invitation.
     *
     * @return The inviter's username.
     */
    public String getInviterUsername() {
        return inviterUsername;
    }

    /**
     * Gets the username of the player who was invited.
     *
     * @return The invited player's username.
     */
    public String getInvitedUsername() {
        return invitedUsername;
    }

    /**
     * Gets the ID of the game associated with the invitation.
     *
     * @return The game ID.
     */
    public int getGameId() {
        return gameId;
    }
}
