package com.andriienko.proxx.application.port.in;

import com.andriienko.proxx.application.dto.GameView;

public interface PlayGameUseCase {
    GameView newGame(int rows, int columns, int blackHoles);

    GameView revealCell(int row, int column);
}
