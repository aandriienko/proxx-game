package com.andriienko.proxx.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import com.andriienko.proxx.common.GameStatus;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.function.Predicate;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter(AccessLevel.PACKAGE)
public class Board {
    private static final int MAX_DIMENSION_SIZE = 100;
    final int rows;
    final int columns;
    final int boardSize;
    final Cell[][] cells;
    int numberOfBlackHoles;
    int numberOfOpenedCells;
    boolean blackHoleOpened;
    GameStatus status;

    Board(int rows, int columns) {
        if (rows < 1 || columns < 1) {
            throw new IllegalArgumentException("Invalid board dimensions. Board should contain at least 1 row and 1 column");
        }
        if (rows > MAX_DIMENSION_SIZE || columns > MAX_DIMENSION_SIZE) {
            throw new IllegalArgumentException(MessageFormat
                    .format("Invalid board dimensions. Board should contain at most {0} rows and {0} columns", MAX_DIMENSION_SIZE));
        }
        this.rows = rows;
        this.columns = columns;
        this.cells = new Cell[rows][columns];
        this.status = GameStatus.IN_PROGRESS;
        this.boardSize = rows * columns;
        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
                cells[row][column] = new Cell(row, column);
            }
        }
    }

    //todo: to service?
    void placeBlackHoles(int numberOfBlackHoles) {
        if (numberOfBlackHoles < 1) {
            throw new IllegalArgumentException("Board should contain at least 1 black hole");
        }
        if (numberOfBlackHoles > boardSize - 1) {
            throw new IllegalArgumentException("Board should contain at least 1 cell");
        }
        Random random = new Random();
        int blackHolesCount = 0;
        while (blackHolesCount < numberOfBlackHoles) {
            Cell cell = getCellAt(random.nextInt(rows), random.nextInt(columns));
            if (placeBlackHoleAt(cell)) {
                blackHolesCount++;
            }
        }
    }

    //places black hole at cell and increase counter on adjacent cells
    boolean placeBlackHoleAt(Cell cell) {
        if (cell.isBlackHole()) {
            return false;
        }
        cell.markAsBlackHole();
        getAdjacentCells(cell)
                .forEach(Cell::addAdjacentBlackHolesCount);
        numberOfBlackHoles++;
        return true;
    }

    public void openCell(Cell cell) {
        if (cell.isBlackHole()) {
            blackHoleOpened = true;
            numberOfOpenedCells = boardSize;
        } else {
            openSafeCells(cell);
        }
        transitToStatus();
    }

    private void openSafeCells(Cell rootCell) {
        if (rootCell.isBlackHole()) {
            return;
        }
        Queue<Cell> cells = new LinkedList<>();
        cells.add(rootCell);
        while (!cells.isEmpty()) {
            Cell cell = cells.poll();
            cell.markAsOpened();
            if (cell.isEmpty()) {
                List<Cell> adjacentCells = getAdjacentCells(cell);
                cells.addAll(adjacentCells.stream()
                        .filter(Predicate.not(Cell::isOpened))
                        .toList());
                adjacentCells.forEach(Cell::markAsOpened);
            }
            numberOfOpenedCells++;
        }
    }

    //todo refactor?
    private void transitToStatus() {
        if (blackHoleOpened) {
            status = GameStatus.LOSE;
            openAll();
        } else if (numberOfBlackHoles + numberOfOpenedCells == boardSize) {
            status = GameStatus.WIN;
        }
    }

    //todo: to service?
    private void openAll() {
        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
                Cell cell = getCellAt(row, column);
                if (!cell.isOpened()) {
                    getCellAt(row, column).markAsOpened();
                }
            }
        }
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
    List<Cell> getAdjacentCells(Cell cell) {
        List<Cell> adjacentCells = new ArrayList<>();
        int minRow = Math.max(0, cell.getRow() - 1);
        int maxRow = Math.min(rows - 1, cell.getRow() + 1);
        int minColumn = Math.max(0, cell.getColumn() - 1);
        int maxColumn = Math.min(columns - 1, cell.getColumn() + 1);

        for (int row = minRow; row <= maxRow; row++) {
            for (int column = minColumn; column <= maxColumn; column++) {
                if (row == cell.getRow() && column == cell.getColumn()) {
                    continue;
                }
                adjacentCells.add(getCellAt(row, column));
            }
        }
        return adjacentCells;
    }

    Cell getCellAt(int row, int column) {
        if (!validBoundaries(row, column)) {
            throw new IllegalArgumentException("Cell is out of board");
        }
        return cells[row][column];
    }

    private boolean validBoundaries(int row, int column) {
        return row >= 0 && column >= 0 && row < rows && column < columns;
    }
}
