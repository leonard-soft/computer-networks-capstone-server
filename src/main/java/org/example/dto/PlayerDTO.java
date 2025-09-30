package org.example.dto;

public class PlayerDTO {
    private int userId;
    private String username;
    private String password;
    private boolean userState;
    private int userMatchWinCount;

    public PlayerDTO(String username, String password){
        this.username = username;
        this.password = password;
        this.userState = false;
        this.userMatchWinCount = 0;
    }

    public PlayerDTO(){}

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean getUserState(){
        return userState;
    }

    public void setUserState(boolean userState){
        this.userState = userState;
    }

    public int getUserMatchWinCount() {
        return userMatchWinCount;
    }

    public void setUserMatchWinCount(int userMatchWinCount) {
        this.userMatchWinCount = userMatchWinCount;
    }

}
