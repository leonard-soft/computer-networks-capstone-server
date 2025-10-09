package org.example.service;

import org.example.dto.GameDTO;
import org.example.dto.GameHasUser;
import org.example.dto.PlayerDTO;
import org.example.dto.RequestPayload;
import org.example.jpa.Queries;

public class RoomService{

    public boolean validateUser(String userName){
        Queries queries = new Queries();
        PlayerDTO player = queries.findPlayerByUsername(userName);
        return player != null;
    }

    public GameDTO registerNewGame(RequestPayload user){
        if (!validateUser(user.username)) return null;
        Queries queries = new Queries();
        GameDTO newGame = queries.createNewGame();
        if(newGame == null) return null;
        return newGame;
    }


    public String registerUserToGame(RequestPayload user, GameDTO game){
        Queries queries = new Queries();
        PlayerDTO userFind = queries.findPlayerByUsername(user.username);
        if(userFind == null) return "User NOT match";
        GameHasUser gameHasUserSaved = queries.registerUserToGame(game, userFind);
        if(gameHasUserSaved == null) return "User NOT Registered to game " + game.getGame_id();
        return "User Registered successfully to game";
    }
}