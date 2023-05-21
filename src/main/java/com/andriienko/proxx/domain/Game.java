package com.andriienko.proxx.domain;

import com.andriienko.proxx.GameStatus;
import lombok.AccessLevel;
import lombok.Getter;

import java.text.MessageFormat;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.Set;

@Getter(AccessLevel.PUBLIC)
public class Game {
    //min playable dimension
    private static final int MIN_DIMENSION_SIZE = 3;

    //I don't see more in existing games of such type
    private static final int MAX_DIMENSION_SIZE = 100;

    private final int size;
    private final Board board;
    private int numberOfOpenedCells;
    private int blackHolesNumber;
    private boolean blackHoleOpened;
    private GameStatus status;

    public Game(int rows, int columns) {
        size = rows * columns;
        validateDimensions(rows, columns);
        board = new Board(rows, columns);
        status = GameStatus.IN_PROGRESS;
    }

    /**
     *     Deferred init improves testability
     */
    public Game start(int blackHolesNumber) {
        placeBlackHoles(board, blackHolesNumber);
        return this;
    }

    private void placeBlackHoles(Board board, int blackHolesNumber) {
        validateBlackHolesNumber(size - 1, blackHolesNumber);
        Random random = new Random();
        int blackHolesCount = 0;
        while (blackHolesCount < blackHolesNumber) {
            if (placeBlackHole(random.nextInt(board.getRows()), random.nextInt(board.getColumns()))) {
                blackHolesCount++;
            }
        }
    }

    public boolean placeBlackHole(int row, int column) {
        Cell cell = board.getCellAt(row, column);
        if (cell.isBlackHole()) {
            return false;
        }
        cell.markAsBlackHole();
        board.visitAdjacentCells(row, column, Cell::addAdjacentBlackHolesCount);
        blackHolesNumber++;
        return true;
    }

    public void openCell(int row, int column) {
        Cell cell = board.getCellAt(row, column);
        if (cell.isBlackHole()) {
            blackHoleOpened = true;
            numberOfOpenedCells = size;
        } else {
            openSafeCells(row, column);
        }
        transitToStatus();
    }

    private void transitToStatus() {
        if (blackHoleOpened) {
            status = GameStatus.LOSE;
            openAll();
        } else if (blackHolesNumber + numberOfOpenedCells == size) {
            status = GameStatus.WIN;
        }
    }

    private void openAll() {
        for (int row = 0; row < board.getRows(); row++) {
            for (int column = 0; column < board.getColumns(); column++) {
                Cell cell = board.getCellAt(row, column);
                if (!cell.isOpened()) {
                    cell.markAsOpened();
                }
            }
        }
    }

    private void openSafeCells(int row, int column) {
        Cell rootCell = board.getCellAt(row, column);
        if (rootCell.isBlackHole()) {
            return;
        }
        Queue<Cell> cells = new LinkedList<>();
        Set<Cell> cellsAdded = new HashSet<>();
        cells.add(rootCell);
        cellsAdded.add(rootCell);
        while (!cells.isEmpty()) {
            Cell cell = cells.poll();
            cell.markAsOpened();
            if (cell.isEmpty() ) {
                board.visitAdjacentCells(cell.getRow(), cell.getColumn(), adjacentCell -> {
                    if (!adjacentCell.isOpened() && !adjacentCell.isBlackHole() && !cellsAdded.contains(adjacentCell)) {
                        cells.add(adjacentCell);
                        cellsAdded.add(adjacentCell);
                    }
                });
            }
            numberOfOpenedCells++;
        }

    }

    private void validateDimensions(int rows, int columns) {
        if (rows < MIN_DIMENSION_SIZE || columns < MIN_DIMENSION_SIZE) {
            throw new IllegalArgumentException(MessageFormat
                    .format("Invalid board dimensions. Board should contain at least {0} rows and {0} columns", MIN_DIMENSION_SIZE));
        }
        if (rows > MAX_DIMENSION_SIZE || columns > MAX_DIMENSION_SIZE) {
            throw new IllegalArgumentException(MessageFormat
                    .format("Invalid board dimensions. Board should contain at most {0} rows and {0} columns", MAX_DIMENSION_SIZE));
        }
    }

    private void validateBlackHolesNumber(int maxBlackHolesNumber, int blackHolesNumber) {
        if (blackHolesNumber < 1) {
            throw new IllegalArgumentException("Board should contain at least 1 black hole");
        }
        if (blackHolesNumber > maxBlackHolesNumber) {
            throw new IllegalArgumentException("Board should contain at least 1 cell");
        }
    }
}
