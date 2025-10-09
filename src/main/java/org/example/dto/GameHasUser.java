package org.example.dto;

import jakarta.persistence.*;

@Entity
@Table(name = "game_has_users")
public class GameHasUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @ManyToMany
    @Column(name = "game_id")
    private GameDTO game_id;
    @ManyToMany
    @Column(name = "user_id")
    private PlayerDTO user_id;
    @Column(name = "users_status")
    private boolean users_status;

    public GameHasUser(int id, GameDTO game_id, PlayerDTO user_id, boolean users_status) {
        this.id = id;
        this.game_id = game_id;
        this.user_id = user_id;
        this.users_status = users_status;
    }

    public GameHasUser() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public GameDTO getGame_id() {
        return game_id;
    }

    public void setGame_id(GameDTO game_id) {
        this.game_id = game_id;
    }

    public PlayerDTO getUser_id() {
        return user_id;
    }

    public void setUser_id(PlayerDTO user_id) {
        this.user_id = user_id;
    }

    public boolean isUsers_status() {
        return users_status;
    }

    public void setUsers_status(boolean users_status) {
        this.users_status = users_status;
    }
}
