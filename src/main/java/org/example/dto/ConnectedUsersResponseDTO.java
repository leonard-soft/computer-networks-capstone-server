package org.example.dto;
public class ConnectedUsersResponseDTO {
    private String[] users;
    public ConnectedUsersResponseDTO(String[] users) {
        this.users = users;
    }
    public String[] getUsers() { return users; }
    public void setUsers(String[] users) { this.users = users; }
}