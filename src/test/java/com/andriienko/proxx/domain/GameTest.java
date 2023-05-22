package com.andriienko.proxx.domain;

import com.andriienko.proxx.enums.GameStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;
import java.util.Set;

import static com.andriienko.proxx.TestUtils.BOARD_DIMENSION_SIZE;
import static com.andriienko.proxx.TestUtils.forEachCell;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GameTest {

    Game game;
    Board board;

    @BeforeEach
    void setUp() {
        game = new Game(3, 3);
        board = game.getBoard();
    }

    @Test
    @DisplayName("Should init variables correctly during creation")
    void shouldInitCorrectly() {
        assertEquals(9, game.getSize());
        assertEquals(8, game.getMaxBlackHolesNumber());
        assertNotNull(board);
        assertEquals(GameStatus.IN_PROGRESS, game.getStatus());
    }

    @Test
    @DisplayName("Places black hole to specified cell")
    void shouldPlaceBlackHoleToCell() {
        Cell cell = board.getCellAt(1, 1);
        game.placeBlackHole(cell.getRow(), cell.getColumn());
        assertEquals(0, cell.getAdjacentBlackHolesCount());
        assertTrue(cell.isBlackHole());
    }

    @ParameterizedTest
    @CsvSource({"0,1", "1,0"})
    @DisplayName("Exception is thrown when wrong lower boundary")
    void shouldThrowExceptionDuringInitWhenWrongRowsLowerBoundary(int rows, int columns) {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Game(rows, columns),
                "Invalid board dimensions. Board should contain at least 1 row and 1 column"
        );
    }

    @ParameterizedTest
    @CsvSource({"101,3", "3,101"})
    @DisplayName("Exception is thrown when wrong upper boundary")
    void shouldThrowExceptionDuringInitWhenWrongRowsUpperBoundary(int rows, int columns) {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Game(rows, columns),
                "Invalid board dimensions. Board should contain at most 100 rows and 100 columns"
        );
    }

    @Test
    @DisplayName("Increases black hole counters when new black hole is placed")
    void shouldIncreaseBlackHoleCounters() {
        forEachCell(board, c -> assertEquals(0, c.getAdjacentBlackHolesCount()));
        List<Cell> cells = List.of(
                board.getCellAt(0, 0),
                board.getCellAt(0, 2),
                board.getCellAt(1, 1),
                board.getCellAt(2, 0),
                board.getCellAt(2, 2)
        );
        cells.forEach(c -> assertTrue(game.placeBlackHole(c.getRow(), c.getColumn())));
        int[][] expectedCounters = new int[][]{
                {1, 3, 1},
                {3, 4, 3},
                {1, 3, 1}
        };
        for (int row = 0; row < BOARD_DIMENSION_SIZE; row++) {
            for (int column = 0; column < BOARD_DIMENSION_SIZE; column++) {
                assertEquals(expectedCounters[row][column], board.getCellAt(row, column).getAdjacentBlackHolesCount());
            }
        }
    }

    @Test
    @DisplayName("Do not increases black hole counters when new black hole is placed again at same cell")
    void shouldNotIncreaseBlackHoleCountersWhenPlacedSeveralTimeOnSameCell() {
        int[][] expectedCounters = new int[][]{
                {1, 1, 1},
                {1, 0, 1},
                {1, 1, 1}
        };

        for (int i = 0; i < 2; i++) {
            boolean result = game.placeBlackHole(1, 1);
            if (i == 0) {
                assertTrue(result);
            } else {
                assertFalse(result);
            }
            for (int row = 0; row < BOARD_DIMENSION_SIZE; row++) {
                for (int column = 0; column < BOARD_DIMENSION_SIZE; column++) {
                    assertEquals(expectedCounters[row][column], board.getCellAt(row, column).getAdjacentBlackHolesCount());
                }
            }
        }
    }

    @Test
    @DisplayName("Exception is thrown if too much black holes placed")
    void shouldFailPlaceMultipleBlackHolesWhenTooMuchBlackHoles() {
        assertThrows(
                IllegalArgumentException.class,
                () -> forEachCell(game.getBoard(), cell -> game.placeBlackHole(cell.getRow(), cell.getColumn())),
                "Board should contain at least 1 cell"
        );
    }

    @Test
    @DisplayName("On black hole open should lose")
    void shouldLSetBlackHoleOpenedFlagAndOpenAllCells() {
        int row = 1;
        int column = 1;
        game.placeBlackHole(row, column);
        forEachCell(board, c -> assertFalse(c.isOpened()));
        assertFalse(game.isBlackHoleOpened());

        game.openCell(row, column);

        forEachCell(board, c -> assertTrue(c.isOpened()));
        assertTrue(game.isBlackHoleOpened());
        assertEquals(GameStatus.LOSE, game.getStatus());
    }

    @Test
    @DisplayName("Should open single cell if root cell non-empty ")
    void shouldOpenSingLeCellIfNonEmpty() {
        game.placeBlackHole(1, 2);

        forEachCell(board, c -> assertFalse(c.isOpened()));
        assertEquals(0, game.getOpenedCellsNumber());


        final Cell rootCell = board.getCellAt(1, 1);
        game.openCell(rootCell.getRow(), rootCell.getColumn());

        forEachCell(board, c -> {
            if (c == rootCell) {
                assertTrue(c.isOpened());
            } else {
                assertFalse(c.isOpened());
            }
        });
        assertEquals(1, game.getOpenedCellsNumber());
        assertEquals(GameStatus.IN_PROGRESS, game.getStatus());
    }

    @Test
    @DisplayName("Opens root cell and its empty neighbors")
    public void shouldOpenRootAndEmptyNeighbors() {
        game.placeBlackHole(1, 2);
        forEachCell(board, c -> assertFalse(c.isOpened()));
        assertEquals(0, game.getOpenedCellsNumber());

        Cell rootCell = board.getCellAt(0, 0);
        game.openCell(rootCell.getRow(), rootCell.getColumn());

        final Set<Cell> expectedOpened = Set.of(rootCell,
                board.getCellAt(1, 0),
                board.getCellAt(2, 0),
                board.getCellAt(0, 1),
                board.getCellAt(1, 1),
                board.getCellAt(2, 1));

        forEachCell(board, c -> {
            if (expectedOpened.contains(c)) {
                assertTrue(c.isOpened());
            } else {
                assertFalse(c.isOpened());
            }
        });
        assertEquals(6, game.getOpenedCellsNumber());
        assertEquals(GameStatus.IN_PROGRESS, game.getStatus());
    }

    @Test
    @DisplayName("Should lead to win when opened all nut black holes")
    public void shouldLeadToWin() {
        /*       0 0 0
                 0 1 1
                 0 1 H
         */
        Cell blackHole = board.getCellAt(2, 2);
        game.placeBlackHole(blackHole.getRow(), blackHole.getColumn());
        forEachCell(board, c -> assertFalse(c.isOpened()));
        assertEquals(0, game.getOpenedCellsNumber());

        game.openCell(0, 0);

        forEachCell(board, c -> {
            if (c == blackHole) {
                assertFalse(c.isOpened());
            } else {
                assertTrue(c.isOpened());
            }
        });
        assertEquals(8, game.getOpenedCellsNumber());
        assertEquals(GameStatus.WIN, game.getStatus());
    }

}
