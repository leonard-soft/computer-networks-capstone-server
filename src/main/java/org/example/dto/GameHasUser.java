package org.example.dto;

import jakarta.persistence.*;
import org.example.entity.Player;

@Entity
@Table(name = "game_has_users")
public class GameHasUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @ManyToOne
    @JoinColumn(name = "game_id")
    private GameDTO game_id;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private Player user_id;
    @Column(name = "users_status")
    private boolean users_status;

    public GameHasUser(int id, GameDTO game_id, Player user_id, boolean users_status) {
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

    public Player getUser_id() {
        return user_id;
    }

    public void setUser_id(Player user_id) {
        this.user_id = user_id;
    }

    public boolean isUsers_status() {
        return users_status;
    }

    public void setUsers_status(boolean users_status) {
        this.users_status = users_status;
    }
}
