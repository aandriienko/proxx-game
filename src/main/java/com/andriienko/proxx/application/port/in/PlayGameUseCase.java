package com.andriienko.proxx.application.port.in;

import com.andriienko.proxx.application.dto.BoardView;
import com.andriienko.proxx.domain.Position;

public interface PlayGameUseCase {
    BoardView newGame(int rows, int columns, int blackHoles);
    BoardView openCell(Position position);
}
