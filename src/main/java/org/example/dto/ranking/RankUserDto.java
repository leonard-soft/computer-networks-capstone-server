package org.example.dto.ranking;

public class RankUserDto {
    private String username;
    private int userMatchWinQuantity;

    public RankUserDto(String username, int userMatchWinQuantity) {
        this.username = username;
        this.userMatchWinQuantity = userMatchWinQuantity;
    }

    public String getUsername() {
        return username;
    }

    public int getUserMatchWinQuantity() {
        return userMatchWinQuantity;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setUserMatchWinQuantity(int userMatchWinQuantity) {
        this.userMatchWinQuantity = userMatchWinQuantity;
    }
}
