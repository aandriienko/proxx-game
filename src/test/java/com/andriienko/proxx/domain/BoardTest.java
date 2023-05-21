package com.andriienko.proxx.domain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static com.andriienko.proxx.TestUtils.forEachCell;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
        forEachCell(board, Assertions::assertNotNull);
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

    @ParameterizedTest
    @MethodSource("adjacencyLists")
    @DisplayName("Visits all adjacency list for cell")
    void shouldReturnAdjacentListForCell(Cell cell, List<Cell> adjacencyList) {
        List<Cell> actualCells = new ArrayList<>();
        board.visitAdjacentCells(cell.getRow(), cell.getColumn(), actualCells::add);
        assertIterableEquals(adjacencyList, actualCells);
    }

    private static Stream<Arguments> adjacencyLists() {
        Board board = new Board(3, 3);
        return Stream.of(
                // Legend: + - cell, * - adjacent cells
                /*
                    + * -
                    * * -
                    - - -
                */
                Arguments.of(board.getCellAt(0, 0),
                        List.of(board.getCellAt(0, 1),
                                board.getCellAt(1, 0),
                                board.getCellAt(1, 1))),

                /*
                   - * +
                   - * *
                   - - -
                */
                Arguments.of(board.getCellAt(0, 2),
                        List.of(board.getCellAt(0, 1),
                                board.getCellAt(1, 1),
                                board.getCellAt(1, 2))),

                /*
                   - - -
                   * * -
                   + * -
                */
                Arguments.of(new Cell(2, 0),
                        List.of(board.getCellAt(1, 0),
                                board.getCellAt(1, 1),
                                board.getCellAt(2, 1))),

                /*
                   - - -
                   - * *
                   - * +
                */
                Arguments.of(board.getCellAt(2, 2),
                        List.of(board.getCellAt(1, 1),
                                board.getCellAt(1, 2),
                                board.getCellAt(2, 1))),

                /*
                   * + *
                   * * *
                   - - -
                */
                Arguments.of(board.getCellAt(0, 1),
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
                Arguments.of(board.getCellAt(1, 0),
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
                Arguments.of(board.getCellAt(1, 2),
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
                Arguments.of(board.getCellAt(2, 1),
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
                Arguments.of(board.getCellAt(1, 1),
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
