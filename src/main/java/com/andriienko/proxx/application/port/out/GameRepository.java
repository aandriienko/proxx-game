package com.andriienko.proxx.application.port.out;

import com.andriienko.proxx.domain.Game;

public interface GameRepository {
    Game get();
    Game save(Game game);
}
