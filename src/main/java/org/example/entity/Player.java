package org.example.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "game_user")
public class Player {
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
    private String userState;
    @Column(name = "user_match_win_quantity")
    private int userMatchWinCount;

    public Player(String username, String password, String passwordSalt){
        this.username = username;
        this.password = password;
        this.passwordSalt = passwordSalt;
        this.userState = "offline";
        this.userMatchWinCount = 0;
    }

    public Player(){}

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

    public String getUserState(){
        return userState;
    }

    public void setUserState(String userState){
        this.userState = userState;
    }

    public int getUserMatchWinCount() {
        return userMatchWinCount;
    }

    public void setUserMatchWinCount(int userMatchWinCount) {
        this.userMatchWinCount = userMatchWinCount;
    }

}
