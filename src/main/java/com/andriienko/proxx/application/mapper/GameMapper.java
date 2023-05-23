package com.andriienko.proxx.application.mapper;

import com.andriienko.proxx.application.dto.BoardView;
import com.andriienko.proxx.application.dto.CellView;
import com.andriienko.proxx.application.dto.GameView;
import com.andriienko.proxx.domain.Board;
import com.andriienko.proxx.domain.Cell;
import com.andriienko.proxx.domain.Game;


public class GameMapper {

    public GameView transformToGameView(Game game) {
        return new GameView(
                game.getRevealedCellsNumber(),
                game.getBlackHolesNumber(),
                game.getSize(),
                game.getStatus(),
                transformToBoardView(game)
        );
    }

    public BoardView transformToBoardView(Game game) {
        Board board = game.getBoard();

        CellView[][] cells = new CellView[board.getRows()][board.getColumns()];
        for (int row = 0; row < board.getRows(); row++) {
            for (int col = 0; col < board.getColumns(); col++) {
                Cell cell = board.getCellAt(row, col);
                cells[row][col] = new CellView(
                        cell.getAdjacentBlackHolesCount(),
                        cell.isBlackHole(),
                        cell.isRevealed(),
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
