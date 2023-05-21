package com.andriienko.proxx.application.port.out;

import com.andriienko.proxx.domain.Board;

import java.util.Optional;

public interface BoardRepository {

    Optional<Board> getActiveBoard();
    Board createBoard(int rows, int columns, int numberOfBlackHoles);
}
