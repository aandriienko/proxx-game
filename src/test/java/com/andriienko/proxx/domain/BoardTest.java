package com.andriienko.proxx.domain;

import com.andriienko.proxx.common.GameStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BoardTest {
    private static final int BOARD_DIMENSION_SIZE = 3;
    Board board;

    @BeforeEach
    void setUp() {
        board = new Board(BOARD_DIMENSION_SIZE, BOARD_DIMENSION_SIZE);
    }

    @Test
    @DisplayName("A board should have correct initial state after creation")
    void shouldHaveCorrectInitialState() {
        assertEquals(BOARD_DIMENSION_SIZE, board.getRows());
        assertEquals(BOARD_DIMENSION_SIZE, board.getColumns());
        assertNotNull(board.getCells());
        assertEquals(GameStatus.IN_PROGRESS, board.getStatus());
        forEachCell(Assertions::assertNotNull);
    }

    @ParameterizedTest
    @CsvSource({"0,1", "1,0"})
    @DisplayName("Exception is thrown when wrong lower boundary")
    void shouldThrowExceptionDuringInitWhenWrongRowsLowerBoundary(int rows, int columns) {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Board(rows, columns),
                "Invalid board dimensions. Board should contain at least 1 row and 1 column"
        );
    }

    @ParameterizedTest
    @CsvSource({"101,1", "1,101"})
    @DisplayName("Exception is thrown when wrong upper boundary")
    void shouldThrowExceptionDuringInitWhenWrongRowsUpperBoundary(int rows, int columns) {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Board(rows, columns),
                "Invalid board dimensions. Board should contain at most 100 rows and 100 columns"
        );

    }

    @ParameterizedTest
    @CsvSource({"-1,0", "0,-1", "3,0", "0,3"})
    @DisplayName("Exception is thrown when getting cell out of board")
    void shouldThrowExceptionWhenGetCellAtWrongPosition(int row, int column) {
        assertThrows(
                IllegalArgumentException.class,
                () -> board.getCellAt(row, column),
                "Cell is out of board"
        );
    }

    @Test
    @DisplayName("Returns adjacency list for cell")
    void shouldReturnAdjacentListForCell() {
        Map<Cell, List<Cell>> adjacencyMap = createAdjacencyMap(board);
        for (Cell cell : adjacencyMap.keySet()) {
            assertIterableEquals(adjacencyMap.get(cell), board.getAdjacentCells(cell.getPosition()));
        }
    }

    @Test
    @DisplayName("Places black hole to specified cell")
    void shouldPlaceBlackHoleToCell() {
        Cell cell = board.getCellAt(1, 1);
        board.placeBlackHole(cell.getPosition());
        assertEquals(0, cell.getAdjacentBlackHolesCount());
        assertTrue(cell.isBlackHole());
    }

    @Test
    @DisplayName("Increases black hole counters when new black hole is placed")
    void shouldIncreaseBlackHoleCounters() {
        forEachCell(c -> {
            assertEquals(0, c.getAdjacentBlackHolesCount());
        });
        List<Cell> cells = List.of(
                board.getCellAt(0, 0),
                board.getCellAt(0, 2),
                board.getCellAt(1, 1),
                board.getCellAt(2, 0),
                board.getCellAt(2, 2)
        );
        cells.forEach(c -> {
            assertTrue(board.placeBlackHole(c.getPosition()));
        });
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
        Cell cell = board.getCellAt(1, 1);
        int[][] expectedCounters = new int[][]{
                {1, 1, 1},
                {1, 0, 1},
                {1, 1, 1}
        };
        for (int i = 0; i < 2; i++) {
            boolean result = board.placeBlackHole(cell.getPosition());
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
    @DisplayName("Places multiple black holes")
    void shouldPlaceMultipleBlackHoles() {
        int expectedBlackHoles = 5;
        board.placeBlackHoles(expectedBlackHoles);
        AtomicInteger actualBlackHoles = new AtomicInteger();
        forEachCell(c -> {
            if (c.isBlackHole()) {
                actualBlackHoles.getAndIncrement();
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
                () -> board.placeBlackHoles(0),
                "Board should contain at least 1 black hole"
        );
    }

    @Test
    @DisplayName("Exception is thrown if too much black holes")
    void shouldFailPlaceMultipleBlackHolesWhenTooMuchBlackHoles() {
        assertThrows(
                IllegalArgumentException.class,
                () -> board.placeBlackHoles(9),
                "Board should contain at least 1 cell"
        );
    }

    @Test
    @DisplayName("On black hole open should lose")
    void shouldLSetBlackHoleOpenedFlagAndOpenAllCells() {
        Position position = new Position(1, 1);
        board.placeBlackHole(position);
        forEachCell(c -> assertFalse(c.isOpened()));
        assertFalse(board.isBlackHoleOpened());

        board.openCell(position);

        forEachCell(c -> assertTrue(c.isOpened()));
        assertTrue(board.isBlackHoleOpened());
        assertEquals(GameStatus.LOSE, board.getStatus());
    }

    private void forEachCell(Consumer<Cell> action) {
        for (int row = 0; row < BOARD_DIMENSION_SIZE; row++) {
            for (int column = 0; column < BOARD_DIMENSION_SIZE; column++) {
                action.accept(board.getCellAt(row, column));
            }
        }
    }

    /*
    void openSafeCells(Cell rootCell) {
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
     */
    @Test
    @DisplayName("Should open single cell if root cell non-empty ")
    void shouldOpenSingLeCellIfNonEmpty() {
        board.placeBlackHole(new Position(1, 2));

        forEachCell(c -> {
            assertFalse(c.isOpened());
        });
        assertEquals(0, board.getNumberOfOpenedCells());

        final Cell rootCell = board.getCellAt(1, 1);
        board.openCell(rootCell.getPosition());

        forEachCell(c -> {
            if (c == rootCell) {
                assertTrue(c.isOpened());
            } else {
                assertFalse(c.isOpened());
            }
        });
        assertEquals(1, board.getNumberOfOpenedCells());
        assertEquals(GameStatus.IN_PROGRESS, board.getStatus());
    }

    @Test
    @DisplayName("Opens root cell and its empty neighbors")
    public void shouldOpenRootAndEmptyNeighbors() {
        board.placeBlackHole(new Position(1, 2));
        forEachCell(c -> assertFalse(c.isOpened()));
        assertEquals(0, board.getNumberOfOpenedCells());

        Cell rootCell = board.getCellAt(0, 0);
        board.openCell(rootCell.getPosition());

        final Set<Cell> expectedOpened = Set.of(rootCell,
                board.getCellAt(1, 0),
                board.getCellAt(2, 0),
                board.getCellAt(0, 1),
                board.getCellAt(1, 1),
                board.getCellAt(2, 1));

        forEachCell(c -> {
            if (expectedOpened.contains(c)) {
                assertTrue(c.isOpened());
            } else {
                assertFalse(c.isOpened());
            }
        });
        assertEquals(6, board.getNumberOfOpenedCells());
        assertEquals(GameStatus.IN_PROGRESS, board.getStatus());
    }

    @Test
    @DisplayName("Should lead to win when opened all nut black holes")
    public void shouldLeadToWin() {
        Cell blackHole = board.getCellAt(2, 2);
        board.placeBlackHole(blackHole.getPosition());
        forEachCell(c -> assertFalse(c.isOpened()));
        assertEquals(0, board.getNumberOfOpenedCells());

        Cell rootCell = board.getCellAt(0, 0);
        board.openCell(rootCell.getPosition());

        forEachCell(c -> {
            if (c == blackHole) {
                assertFalse(c.isOpened());
            } else {
                assertTrue(c.isOpened());
            }
        });
        assertEquals(8, board.getNumberOfOpenedCells());
        assertEquals(GameStatus.WIN, board.getStatus());
    }

    private Map<Cell, List<Cell>> createAdjacencyMap(Board board) {
        return Map.ofEntries(
                // Legend: + - cell, * - adjacent cells
                /*
                    + * -
                    * * -
                    - - -
                */
                Map.entry(board.getCellAt(0, 0),
                        List.of(board.getCellAt(0, 1),
                                board.getCellAt(1, 0),
                                board.getCellAt(1, 1))),

                /*
                   - * +
                   - * *
                   - - -
                */
                Map.entry(board.getCellAt(0, 2),
                        List.of(board.getCellAt(0, 1),
                                board.getCellAt(1, 1),
                                board.getCellAt(1, 2))),

                /*
                   - - -
                   * * -
                   + * -
                */
                Map.entry(new Cell(2, 0),
                        List.of(board.getCellAt(1, 0),
                                board.getCellAt(1, 1),
                                board.getCellAt(2, 1))),

                /*
                   - - -
                   - * *
                   - * +
                */
                Map.entry(board.getCellAt(2, 2),
                        List.of(board.getCellAt(1, 1),
                                board.getCellAt(1, 2),
                                board.getCellAt(2, 1))),

                /*
                   * + *
                   * * *
                   - - -
                */
                Map.entry(board.getCellAt(0, 1),
                        List.of(board.getCellAt(0, 0),
                                board.getCellAt(0, 2),
                                board.getCellAt(1, 0),
                                board.getCellAt(1, 1),
                                board.getCellAt(1, 2)
                        )),


             /*
                * * -
                + * -
                * * -
             */
                Map.entry(board.getCellAt(1, 0),
                        List.of(board.getCellAt(0, 0),
                                board.getCellAt(0, 1),
                                board.getCellAt(1, 1),
                                board.getCellAt(2, 0),
                                board.getCellAt(2, 1)
                        )),


                /*
                   - * *
                   - * +
                   - * *
                */
                Map.entry(board.getCellAt(1, 2),
                        List.of(board.getCellAt(0, 1),
                                board.getCellAt(0, 2),
                                board.getCellAt(1, 1),
                                board.getCellAt(2, 1),
                                board.getCellAt(2, 2)
                        )),


                /*
                   - - -
                   * * *
                   * + *
                */
                Map.entry(board.getCellAt(2, 1),
                        List.of(board.getCellAt(1, 0),
                                board.getCellAt(1, 1),
                                board.getCellAt(1, 2),
                                board.getCellAt(2, 0),
                                board.getCellAt(2, 2)
                        )),


                /*
                 * * *
                 * + *
                 * * *
                 */
                Map.entry(board.getCellAt(1, 1),
                        List.of(board.getCellAt(0, 0),
                                board.getCellAt(0, 1),
                                board.getCellAt(0, 2),
                                board.getCellAt(1, 0),
                                board.getCellAt(1, 2),
                                board.getCellAt(2, 0),
                                board.getCellAt(2, 1),
                                board.getCellAt(2, 2)
                        ))
        );

    }
}
