package com.andriienko.proxx.application.service;

import com.andriienko.proxx.application.dto.BoardView;
import com.andriienko.proxx.application.dto.CellView;
import com.andriienko.proxx.application.port.in.PlayGameUseCase;
import com.andriienko.proxx.application.port.out.BoardRepository;
import com.andriienko.proxx.domain.Board;
import com.andriienko.proxx.domain.Cell;
import com.andriienko.proxx.domain.Position;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProxxGameService implements PlayGameUseCase {

    BoardRepository boardRepository;

    public BoardView newGame(int rows, int columns, int blackHoles) {
        Board board = boardRepository.createBoard(rows, columns, blackHoles);
        return transformToView(board);
    }

    public BoardView openCell(Position position) {
        Board board = boardRepository.getActiveBoard()
                .orElseThrow(() -> new IllegalStateException("No active board found"));
        board.openCell(position);
        return transformToView(board);
    }

    private BoardView transformToView(Board board) {
        CellView[][] cells = new CellView[board.getRows()][board.getColumns()];
        for (int row = 0; row < board.getRows(); row++) {
            for (int col = 0; col < board.getColumns(); col++) {
                Cell cell = board.getCellAt(row, col); //todo: could replace to position?
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
                board.getStatus(),
                cells
        );
    }

}
