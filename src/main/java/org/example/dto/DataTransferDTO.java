package org.example.dto;

import java.util.Map;

public class DataTransferDTO {

    private int IdPlayer;
    private String eventType; // e.g., "PLAYER_MOVE", "PLAYER_ATTACK"
    private Map<String, Object> payload;

    public DataTransferDTO(int idPlayer, String eventType, Map<String, Object> payload) {
        this.IdPlayer = idPlayer;
        this.eventType = eventType;
        this.payload = payload;
    }

    public DataTransferDTO() {
    }

    public int getIdPlayer() {
        return IdPlayer;
    }

    public void setIdPlayer(int idPlayer) {
        IdPlayer = idPlayer;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public Map<String, Object> getPayload() {
        return payload;
    }

    public void setPayload(Map<String, Object> payload) {
        this.payload = payload;
    }
}