package com.andriienko.proxx.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.concurrent.atomic.AtomicReference;

import static com.andriienko.proxx.TestUtils.BOARD_DIMENSION_SIZE;
import static com.andriienko.proxx.TestUtils.forEachCell;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class BoardFactoryTest {
    BoardFactory boardFactory;
    @BeforeEach
    void setUp() {
        boardFactory = new BoardFactory();
    }

    @ParameterizedTest
    @CsvSource({"0,1", "1,0"})
    @DisplayName("Exception is thrown when wrong lower boundary")
    void shouldThrowExceptionDuringInitWhenWrongRowsLowerBoundary(int rows, int columns) {
        assertThrows(
                IllegalArgumentException.class,
                () -> boardFactory.create(rows, columns, 1),
                "Invalid board dimensions. Board should contain at least 1 row and 1 column"
        );
    }

    @ParameterizedTest
    @CsvSource({"101,1", "1,101"})
    @DisplayName("Exception is thrown when wrong upper boundary")
    void shouldThrowExceptionDuringInitWhenWrongRowsUpperBoundary(int rows, int columns) {
        assertThrows(
                IllegalArgumentException.class,
                () -> boardFactory.create(rows, columns, 1),
                "Invalid board dimensions. Board should contain at most 100 rows and 100 columns"
        );
    }

    @Test
    @DisplayName("Places multiple random black holes")
    void shouldPlaceMultipleRandomBlackHoles() {
        int expectedBlackHoles = 5;
        Board board = boardFactory.create(BOARD_DIMENSION_SIZE, BOARD_DIMENSION_SIZE, expectedBlackHoles);
        AtomicReference<Integer> actualBlackHoles = new AtomicReference<>(0);
        forEachCell(board, c -> {
            if (c.isBlackHole()) {
                actualBlackHoles.getAndSet(actualBlackHoles.get() + 1);
            }
        });
        assertEquals(expectedBlackHoles, actualBlackHoles.get());
        assertEquals(expectedBlackHoles, board.getNumberOfBlackHoles());
    }

    @Test
    @DisplayName("Exception is thrown if insufficient black holes")
    void shouldFailPlaceMultipleBlackHolesWhenInsufficientBlackHoles() {
        assertThrows(
                IllegalArgumentException.class,
                () -> boardFactory.create(BOARD_DIMENSION_SIZE, BOARD_DIMENSION_SIZE,0),
                "Board should contain at least 1 black hole"
        );
    }

    @Test
    @DisplayName("Exception is thrown if too much black holes")
    void shouldFailPlaceMultipleBlackHolesWhenTooMuchBlackHoles() {
        assertThrows(
                IllegalArgumentException.class,
                () -> boardFactory.create(BOARD_DIMENSION_SIZE, BOARD_DIMENSION_SIZE, 9),
                "Board should contain at least 1 cell"
        );
    }
}
