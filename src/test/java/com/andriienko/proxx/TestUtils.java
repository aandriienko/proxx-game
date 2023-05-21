package com.andriienko.proxx;

import com.andriienko.proxx.domain.Board;
import com.andriienko.proxx.domain.Cell;

import java.util.function.Consumer;

public class TestUtils {
    public static final int BOARD_DIMENSION_SIZE = 3;

    public static void forEachCell(Board board, Consumer<Cell> action) {
        for (int row = 0; row < BOARD_DIMENSION_SIZE; row++) {
            for (int column = 0; column < BOARD_DIMENSION_SIZE; column++) {
                action.accept(board.getCellAt(row, column));
            }
        }
    }
}
