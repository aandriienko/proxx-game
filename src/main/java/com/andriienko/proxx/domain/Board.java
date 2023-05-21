package com.andriienko.proxx.domain;

import lombok.AccessLevel;
import lombok.Getter;

import java.util.function.Consumer;


@Getter(AccessLevel.PACKAGE)
public class Board {
    @Getter(AccessLevel.PUBLIC)
    private final int rows;

    @Getter(AccessLevel.PUBLIC)
    private final int columns;
    private final Cell[][] cells;

    Board(int rows, int columns) {
        this.rows = rows;
        this.columns = columns;
        this.cells = new Cell[rows][columns];

        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
                cells[row][column] = new Cell(row, column);
            }
        }
    }

    public Cell getCellAt(int row, int column) {
        if (!validBoundaries(row, column)) {
            throw new IllegalArgumentException("Cell is out of board");
        }
        return cells[row][column];
    }

    private boolean validBoundaries(int row, int column) {
        return row >= 0 && column >= 0 && row < rows && column < columns;
    }

    /*
           N.W   N   N.E
              \   |   /
               \  |  /
            W----Cell----E
                 / | \
               /   |  \
            S.W    S   S.E

       Cell-->Current Cell (row, col)

        N.W--> North-West   (row-1, col-1)
        N -->  North        (row-1, col)
        N.E--> North-East   (row-1, col+1)
        W -->  West         (row, col-1)
        E -->  East         (row, col+1)
        S.W--> South-West   (row+1, col-1)
        S -->  South        (row+1, col)
        S.E--> South-East   (row+1, col+1)
    */
    //business rule
    public void visitAdjacentCells(int row, int column, Consumer<Cell> action) {
        int minRow = Math.max(0, row - 1);
        int maxRow = Math.min(rows - 1, row + 1);
        int minColumn = Math.max(0, column - 1);
        int maxColumn = Math.min(columns - 1, column + 1);

        for (int r = minRow; r <= maxRow; r++) {
            for (int c = minColumn; c <= maxColumn; c++) {
                if (r == row && c == column) {
                    continue;
                }
                action.accept(getCellAt(r, c));
            }
        }
    }
}
