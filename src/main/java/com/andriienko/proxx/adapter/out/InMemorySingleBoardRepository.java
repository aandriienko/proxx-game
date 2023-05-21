package com.andriienko.proxx.adapter.out;

import com.andriienko.proxx.application.port.out.BoardRepository;
import com.andriienko.proxx.common.GameStatus;
import com.andriienko.proxx.domain.Board;
import com.andriienko.proxx.domain.BoardFactory;
import lombok.AllArgsConstructor;

import java.util.Optional;

@AllArgsConstructor
public class InMemorySingleBoardRepository implements BoardRepository {
    private static Board board;
    private final BoardFactory boardFactory;

    @Override
    public Optional<Board> getActiveBoard() {
        if (board != null && board.getStatus() == GameStatus.IN_PROGRESS) { //todo: isGameOver {
            return Optional.of(board);
        }
        return Optional.empty();
    }

    @Override
    public Board createBoard(int rows, int columns, int numberOfBlackHoles) {
        board = boardFactory.create(rows, columns, numberOfBlackHoles);
        return board;
    }
}
