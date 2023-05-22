package com.andriienko.proxx.application.service;

import com.andriienko.proxx.application.dto.BoardView;
import com.andriienko.proxx.application.dto.CellView;
import com.andriienko.proxx.application.dto.GameView;
import com.andriienko.proxx.application.port.in.PlayGameUseCase;
import com.andriienko.proxx.application.port.out.GameRepository;
import com.andriienko.proxx.domain.Board;
import com.andriienko.proxx.domain.Cell;
import com.andriienko.proxx.domain.Game;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ProxxGameService implements PlayGameUseCase {
    private final GameRepository gameRepository;

    public GameView newGame(int rows, int columns, int blackHoles) {
        Game game = new Game(rows, columns);
        gameRepository.save(game.start(blackHoles));
        return transformToGameView(game);
    }

    public GameView openCell(int row, int column) {
        Game game = gameRepository.get();
        game.openCell(row, column);
        return transformToGameView(game);
    }

    private GameView transformToGameView(Game game) {
        return new GameView(
                game.getNumberOfOpenedCells(),
                game.getBlackHolesNumber(),
                game.getSize(),
                game.getStatus(),
                transformToBoardView(game)
        );
    }

    private BoardView transformToBoardView(Game game) {
        Board board = game.getBoard();

        CellView[][] cells = new CellView[board.getRows()][board.getColumns()];
        for (int row = 0; row < board.getRows(); row++) {
            for (int col = 0; col < board.getColumns(); col++) {
                Cell cell = board.getCellAt(row, col);
                cells[row][col] = new CellView(
                        cell.getAdjacentBlackHolesCount(),
                        cell.isBlackHole(),
                        cell.isOpened(),
                        cell.isEmpty()
                );
            }
        }
        return new BoardView(
                board.getRows(),
                board.getColumns(),
                cells
        );
    }

}
