package org.example.dto;

import jakarta.persistence.*;

@Entity
@Table(name = "game_match")
public class GameDTO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int game_id;
    @Column(name = "game_status ")
    private boolean game_status;

    public GameDTO(int game_id, boolean game_status) {
        this.game_id = game_id;
        this.game_status = game_status;
    }

    public GameDTO() {
    }

    public int getGame_id() {
        return game_id;
    }

    public void setGame_id(int game_id) {
        this.game_id = game_id;
    }

    public boolean isGame_status() {
        return game_status;
    }

    public void setGame_status(boolean game_status) {
        this.game_status = game_status;
    }
}
