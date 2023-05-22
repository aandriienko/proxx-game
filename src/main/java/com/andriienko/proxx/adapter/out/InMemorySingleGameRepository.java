package com.andriienko.proxx.adapter.out;

import com.andriienko.proxx.application.port.out.GameRepository;
import com.andriienko.proxx.domain.Game;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class InMemorySingleGameRepository implements GameRepository {
    private Game game;

    @Override
    public Game get() {
        if (game == null) {
            throw new IllegalStateException("There is no initialized board");
        }
        return game;
    }

    @Override
    public Game save(Game game) {
        this.game = game;
        return game;
    }
}
