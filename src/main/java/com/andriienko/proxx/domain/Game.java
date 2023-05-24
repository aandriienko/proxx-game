package com.andriienko.proxx.domain;

import com.andriienko.proxx.enums.GameStatus;
import lombok.AccessLevel;
import lombok.Getter;

import java.text.MessageFormat;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

/**
 * The Game class represents a game of Proxx.
 * It's responsible for managing the game state, including the placement of black holes and revealing cells, as well as determining the status of the game.
 */
@Getter(AccessLevel.PUBLIC)
public class Game {

    private static final int MIN_DIMENSION_SIZE = 3;
    private static final int MAX_DIMENSION_SIZE = 100;

    private final int size;
    private final Board board;
    private final int maxBlackHolesNumber;
    private int revealedCellsNumber;
    private int blackHolesNumber;
    private boolean blackHoleRevealed;
    private GameStatus status;

    Game(int rows, int columns) {
        size = rows * columns;
        maxBlackHolesNumber = size - 1;
        validateDimensions(rows, columns);
        board = new Board(rows, columns);
        status = GameStatus.IN_PROGRESS;
    }

    /**
     * Places single black hole on board and increases black hole counters for all adjacent cells
     *
     * @param row - zero based row coordinate
     * @param column - zero based column coordinate
     * @return false - when cell is already black hole, otherwise - true
     */
    public boolean placeBlackHole(int row, int column) {
        if (blackHolesNumber >= maxBlackHolesNumber) {
            throw new IllegalArgumentException("Too much mines. Board should contain at least 1 cell");
        }
        Cell cell = board.getCellAt(row, column);
        if (cell.isBlackHole()) {
            return false;
        }
        cell.markAsBlackHole();
        board.visitAdjacentCells(row, column, Cell::addAdjacentBlackHolesCount);
        blackHolesNumber++;
        return true;
    }

    /**
     * Reveals the cell at row,column.<p>
     * 1) For black holes just sets internal variables, that is used for status transition evaluation<p>
     * 2) For non-black holes(safe) calls revealSafeCell method
     * @param row - zero based row number coordinate
     * @param column - zero based column number coordinate
     */
    public void revealCell(int row, int column) {
        Cell cell = board.getCellAt(row, column);
        if (cell.isBlackHole()) {
            blackHoleRevealed = true;
            revealedCellsNumber = size;
        } else {
            revealSafeCells(row, column);
        }
        transitToStatus();
    }

    /**
     *  Manages game status:
     *  <p>1. If black hole was opened - transits to LOSE status and reveals all cells
     *  <p>2. If all cells were revealed (except black holes) - transits to WIN status
     */
    private void transitToStatus() {
        if (blackHoleRevealed) {
            status = GameStatus.LOSE;
            revealAll();
        } else if (blackHolesNumber + revealedCellsNumber == size) {
            status = GameStatus.WIN;
        }
    }

    /**
     *  Reveals all cells, without any status transition. Uses when black hole was opened
     */
    private void revealAll() {
        for (int row = 0; row < board.getRows(); row++) {
            for (int column = 0; column < board.getColumns(); column++) {
                Cell cell = board.getCellAt(row, column);
                if (!cell.isRevealed()) {
                    cell.markAsRevealed();
                }
            }
        }
    }

    /**
     * Reveals only safe (non-black hole) cell.
     * <p>1) For non-empty cell - just opens it.
     * <p>2) For empty cell - uses BFS algorithm to open all connected empty cells
     * @param row - zero based row coordinate
     * @param column - zero based column coordinate
     */
    private void revealSafeCells(int row, int column) {
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
            if(cell.isRevealed()) {
                continue;
            }
            if (cell.isEmpty()) {
                board.visitAdjacentCells(cell.getRow(), cell.getColumn(), adjacentCell -> {
                    if (!adjacentCell.isRevealed() && !adjacentCell.isBlackHole() && !cellsAdded.contains(adjacentCell)) {
                        cells.add(adjacentCell);
                        cellsAdded.add(adjacentCell);
                    }
                });
            }
            cell.markAsRevealed();
            revealedCellsNumber++;
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
}
