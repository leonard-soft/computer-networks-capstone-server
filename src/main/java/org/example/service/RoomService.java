package org.example.service;

import org.example.dto.GameDTO;
import org.example.dto.GameHasUser;
import org.example.dto.PlayerDTO;
import org.example.dto.RequestPayload;
import org.example.jpa.Queries;

public class RoomService{

    /**
     * Function that validates if the user exists, if it exists it
     * returns true but if not it returns false
     *
     * @param userName user's username
     * @return boolean true if it exists, but otherwise returns false
     */
    public boolean validateUser(String userName){
        Queries queries = new Queries();
        PlayerDTO player = queries.findPlayerByUsername(userName);
        return player != null;
    }

    /**
     * Function that creates a new game in the database and returns
     * the created game.
     *
     * @param user to validate that it exists
     *
     * @return GameDTO so that this is passed later to register
     * the user to the game
     */
    public GameDTO registerNewGame(RequestPayload user){
        if (!validateUser(user.username)) return null;
        Queries queries = new Queries();
        GameDTO newGame = queries.createNewGame();
        if(newGame == null) return null;
        return newGame;
    }


    /**
     * Function to create a new record in the database which
     * registers a user to a game
     *
     * @param user to reference which user is registered in the game
     * @param game In order to know which game the user is registered
     * for, it must be previously created.
     *
     * @return a String to validate whether the user was registered
     * correctly in the game or not
     */
    public String registerUserToGame(RequestPayload user, GameDTO game){
        Queries queries = new Queries();
        PlayerDTO userFind = queries.findPlayerByUsername(user.username);
        if(userFind == null) return "User NOT match";
        GameHasUser gameHasUserSaved = queries.registerUserToGame(game, userFind);
        if(gameHasUserSaved == null) return "User NOT Registered to game " + game.getGame_id();
        return "User Registered successfully to game";
    }
}