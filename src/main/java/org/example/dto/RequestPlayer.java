package org.example.dto;

public class RequestPlayer {
    private String type;
    private PlayerDTO player;

    public RequestPlayer(String type, PlayerDTO player){
        this.type = type;
        this.player = player;
    }

    public RequestPlayer(){}

    public String getType() {
        return type;
    }

    public PlayerDTO getPlayer() {
        return player;
    }

    public void setPlayer(PlayerDTO player) {
        this.player = player;
    }
    
}
