package org.example.dto;

import jakarta.persistence.*;

@Entity
@Table(name = "game_user")
public class PlayerDTO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int userId;
    @Column(name = "username")
    private String username;
    @Column(name = "password")
    private String password;
    @Column
    private String passwordSalt;
    @Column(name = "user_state")
    private boolean userState;
    @Column(name = "user_match_win_quantity")
    private int userMatchWinCount;

    public PlayerDTO(String username, String password, String passwordSalt){
        this.username = username;
        this.password = password;
        this.passwordSalt = passwordSalt;
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
