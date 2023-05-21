package com.andriienko.proxx.domain;

import com.andriienko.proxx.common.GameStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.function.Predicate;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter(AccessLevel.PACKAGE)
public class Board {
    @Getter(AccessLevel.PUBLIC)
    final int rows;

    @Getter(AccessLevel.PUBLIC)
    final int columns;
    final int size;
    final Cell[][] cells;
    int numberOfBlackHoles;
    int numberOfOpenedCells;
    boolean blackHoleOpened;

    @Getter(AccessLevel.PUBLIC)
    GameStatus status;

    Board(int rows, int columns) {
        this.rows = rows;
        this.columns = columns;
        this.cells = new Cell[rows][columns];
        this.status = GameStatus.IN_PROGRESS;
        this.size = rows * columns;
        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
                cells[row][column] = new Cell(row, column);
            }
        }
    }

    //places black hole at cell and increase counter on adjacent cells
    boolean placeBlackHole(Position position) {
        Cell cell = getCellAt(position);
        if (cell.isBlackHole()) {
            return false;
        }
        cell.markAsBlackHole();
        getAdjacentCells(position)
                .forEach(Cell::addAdjacentBlackHolesCount);
        numberOfBlackHoles++;
        return true;
    }

    public void openCell(Position position) {
        Cell cell = getCellAt(position);
        if (cell.isBlackHole()) {
            blackHoleOpened = true;
            numberOfOpenedCells = size;
        } else {
            openSafeCells(position);
        }
        transitToStatus();
    }

    private void openSafeCells(Position position) {
        Cell rootCell = getCellAt(position);
        if (rootCell.isBlackHole()) {
            return;
        }
        Queue<Cell> cells = new LinkedList<>();
        cells.add(rootCell);
        while (!cells.isEmpty()) {
            Cell cell = cells.poll();
            cell.markAsOpened();
            if (cell.isEmpty()) {
                List<Cell> adjacentCells = getAdjacentCells(cell.getPosition());
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
        } else if (numberOfBlackHoles + numberOfOpenedCells == size) {
            status = GameStatus.WIN;
        }
    }

    //todo: to service?
    private void openAll() {
        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
                Cell cell = getCellAt(row, column);
                if (!cell.isOpened()) {
                    cell.markAsOpened();
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
    List<Cell> getAdjacentCells(Position position) {
        List<Cell> adjacentCells = new ArrayList<>();
        int cellRow = position.getRow();
        int cellColumn = position.getColumn();

        int minRow = Math.max(0, cellRow - 1);
        int maxRow = Math.min(rows - 1, cellRow + 1);
        int minColumn = Math.max(0, cellColumn - 1);
        int maxColumn = Math.min(columns - 1, cellColumn + 1);

        for (int row = minRow; row <= maxRow; row++) {
            for (int column = minColumn; column <= maxColumn; column++) {
                if (row == cellRow && column == cellColumn) {
                    continue;
                }
                adjacentCells.add(getCellAt(row, column));
            }
        }
        return adjacentCells;
    }

    Cell getCellAt(Position position) {
        return getCellAt(position.getRow(), position.getColumn());
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
}
