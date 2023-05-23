package com.andriienko.proxx.adapter.out;

import com.andriienko.proxx.application.port.out.GameRepository;
import com.andriienko.proxx.domain.Game;
import lombok.NoArgsConstructor;

/**
 * This is a repository class, a key part of the Persistence Layer.
 * Repository provides an abstraction layer over the underlying storage mechanism,
 * which decouples the application logic from the infrastructure concerns.
 * This keeps Service Layer clean and independent of the specific persistence technology.
 *<p>
 * In this case, InMemorySingleGameRepository is providing a memory storage mechanism.
 *<p>
 * Even in a simple game like this, utilizing a repository can be beneficial.
 * Imagine you decided to introduce features like maintaining player scores or a top 10 leaderboard.
 * These enhancements would require preserving the game state, something that a repository can handle effectively.
*/
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
