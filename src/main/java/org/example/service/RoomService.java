package org.example.service;

import org.example.dto.GameDTO;
import org.example.dto.GameHasUser;
import org.example.entity.Player;
import org.example.jpa.Queries;
import org.example.logs.ManageLogs;

public class RoomService {
    private final ManageLogs manageLogs = new ManageLogs();

    /**
     * Creates a new game and registers the host with users_status = true.
     * @param hostUsername The username of the host creating the game.
     * @return GameDTO The created game.
     * @throws IllegalArgumentException If the host is not found.
     */
    public GameDTO createGameAndRegisterHost(String hostUsername) {
        Queries queries = new Queries();
        Player host = queries.findPlayerByUsername(hostUsername);
        if (host == null) {
            manageLogs.saveLog("WARN", "Host not found for game creation: " + hostUsername);
            throw new IllegalArgumentException("Host not found");
        }
        GameDTO game = queries.registerNewGame();
        queries.registerUserToGame(game, host, true);
        manageLogs.saveLog("INFO", "Game created by host " + hostUsername + " with ID: " + game.getGame_id());
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
            manageLogs.saveLog("WARN", "Invitee not found for invitation: " + inviteeUsername);
            return "User not found";
        }
        GameDTO game = queries.findGameById(gameId);
        if (game == null) {
            manageLogs.saveLog("WARN", "Game not found for invitation: " + gameId);
            return "Game not found";
        }
        GameHasUser invitation = queries.registerUserToGame(game, invitee, false);
        if (invitation == null) {
            manageLogs.saveLog("ERROR", "Invitation could not be created for game " + gameId);
            return "Invitation could not be created for game " + gameId;
        }
        manageLogs.saveLog("INFO", "Invitation sent to " + inviteeUsername + " for game " + gameId);
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
            manageLogs.saveLog("WARN", "No pending invitation found for user " + inviteeUsername + " in game " + gameId);
            return "No pending invitation found.";
        }
        if (accepted) {
            invitation.setUsers_status(true);
            queries.updateInvitation(invitation);
            manageLogs.saveLog("INFO", "User " + inviteeUsername + " accepted invitation for game " + gameId);
            return "Invitation accepted.";
        } else {
            queries.deleteInvitation(invitation);
            manageLogs.saveLog("INFO", "User " + inviteeUsername + " declined invitation for game " + gameId);
            return "Invitation declined.";
        }
    }
}