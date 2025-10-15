package org.example.service;

import org.example.dto.GameDTO;
import org.example.dto.GameHasUser;
import org.example.entity.Player;
import org.example.jpa.Queries;

public class RoomService{
    /**
     * Creates a new game and registers the host with users_status = true.
     * @param hostUsername The username of the host creating the game.
     * @return GameDTO The created game.
     * @throws IllegalArgumentException If the host is not found.
     */
    public GameDTO createGameAndRegisterHost(String hostUsername){
        Queries queries = new Queries();
        Player host = queries.findPlayerByUsername(hostUsername);
        if (host == null){
            throw new IllegalArgumentException("Host not found");
        }
        GameDTO game = queries.registerNewGame();
        queries.registerUserToGame(game, host, true);
        return game;
    }

    /**
     * Sends an invitation to a user for a specific game with users_status = false.
     * @param gameId The ID of the game.
     * @param inviteeUsername The username of the invited user.
     * @return String A message indicating success or failure.
     */
    public String createInvitation(int gameId, String inviteeUsername) {
        Queries queries = new Queries();
        Player invitee = queries.findPlayerByUsername(inviteeUsername);
        if (invitee == null) {
            return "User not found";
        }
        GameDTO game = queries.findGameById(gameId);
        if (game == null) {
            return "Game not found";
        }
        GameHasUser invitation = queries.registerUserToGame(game, invitee, false);
        if (invitation == null) {
            return "Invitation could not be created for game " + gameId;
        }
        return "Invitation sent successfully";
    }

    /**
     * Handles the response to an invitation (accept or reject).
     * @param gameId The ID of the game.
     * @param inviteeUsername The username of the invited user.
     * @param accepted True to accept, false to reject.
     * @return String A message indicating the result.
     */
    public String respondToInvitation(int gameId, String inviteeUsername, boolean accepted) {
        Queries queries = new Queries();
        GameHasUser invitation = queries.findInvitation(gameId, inviteeUsername);
        if (invitation == null) {
            return "No pending invitation found.";
        }
        if (accepted) {
            invitation.setUsers_status(true);
            queries.updateInvitation(invitation);
            return "Invitation accepted.";
        } else {
            queries.deleteInvitation(invitation);
            return "Invitation declined.";
        }
    }
}