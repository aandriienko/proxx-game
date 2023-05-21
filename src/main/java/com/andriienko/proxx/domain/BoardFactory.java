package com.andriienko.proxx.domain;

import java.text.MessageFormat;
import java.util.Random;

public class BoardFactory {
    private static final int MAX_DIMENSION_SIZE = 100;
    public Board create(int rows, int columns, int blackHolesNumber) {
        validateDimensions(rows, columns);
        validateBlackHolesNumber(rows * columns - 1, blackHolesNumber);
        Board board = new Board(rows, columns);
        placeBlackHoles(board, blackHolesNumber);
        return board;
    }

    private void validateDimensions(int rows, int columns) {
        if (rows < 1 || columns < 1) {
            throw new IllegalArgumentException("Invalid board dimensions. Board should contain at least 1 row and 1 column");
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
    
    private void placeBlackHoles(Board board, int blackHolesNumber) {
        Random random = new Random();
        int blackHolesCount = 0;
        while (blackHolesCount < blackHolesNumber) {
            Position position = new Position(random.nextInt(board.getRows()), random.nextInt(board.getColumns()));
            if (board.placeBlackHole(position)) {
                blackHolesCount++;
            }
        }
    }
}
