package org.example.dto;

import java.io.Serializable;

public class DataTransferDTO implements Serializable {

    private int IdPlayer;
    private double positionX;
    private double positionY;
    private String state;
    private String dir;

    public DataTransferDTO(int IdPlayer, double positionX, double positionY, String state, String dir) {
        this.IdPlayer = IdPlayer;
        this.positionX = positionX;
        this.positionY = positionY;
        this.state = state;
        this.dir = dir;
    }

    public DataTransferDTO() {
    }

    public int getIdPlayer() {
        return IdPlayer;
    }

    public void setIdPlayer(int idPlayer) {
        IdPlayer = idPlayer;
    }

    public double getPositionX() {
        return positionX;
    }

    public void setPositionX(double positionX) {
        this.positionX = positionX;
    }

    public double getPositionY() {
        return positionY;
    }

    public void setPositionY(double positionY) {
        this.positionY = positionY;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getDir() {
        return dir;
    }

    public void setDir(String dir) {
        this.dir = dir;
    }
}
