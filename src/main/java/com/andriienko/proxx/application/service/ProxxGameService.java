package com.andriienko.proxx.application.service;

import com.andriienko.proxx.application.dto.GameView;
import com.andriienko.proxx.application.mapper.GameMapper;
import com.andriienko.proxx.application.port.in.PlayGameUseCase;
import com.andriienko.proxx.application.port.out.GameRepository;
import com.andriienko.proxx.domain.Game;
import com.andriienko.proxx.domain.GameFactory;
import lombok.AllArgsConstructor;

/**
 *   This is a Service(Application) Layer class, which primarily handles business logic and coordinating operations between the Domain and Data Access Layers.
 *   It also ensures that changes in one part of the system (e.g., how games are persisted) don't directly affect others (e.g., game logic).
 */
@AllArgsConstructor
public class ProxxGameService implements PlayGameUseCase {

    private final GameFactory gameFactory;
    private final GameRepository gameRepository;
    private final GameMapper gameMapper;

    public GameView newGame(int rows, int columns, int blackHoles) {
        Game game = gameFactory.createGameWithRandomlyDistributedHoles(rows, columns, blackHoles);
        gameRepository.save(game);
        return gameMapper.transformToGameView(game);
    }

    public GameView revealCell(int row, int column) {
        Game game = gameRepository.get();
        game.revealCell(row, column);
        return gameMapper.transformToGameView(game);
    }
}
