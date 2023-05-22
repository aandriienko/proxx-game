package com.andriienko.proxx.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static com.andriienko.proxx.TestUtils.BOARD_DIMENSION_SIZE;
import static com.andriienko.proxx.TestUtils.forEachCell;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class GameFactoryTest {

    GameFactory gameFactory;

    @BeforeEach
    void setUp() {
        gameFactory = new GameFactory();
    }

    @Test
    @DisplayName("Places multiple random black holes")
    void shouldPlaceMultipleRandomBlackHoles() {
        int expectedBlackHoles = 5;

        Game game = gameFactory.createGameWithRandomlyDistributedHoles(BOARD_DIMENSION_SIZE, BOARD_DIMENSION_SIZE, expectedBlackHoles);
        AtomicInteger actualBlackHoles = new AtomicInteger();
        forEachCell(game.getBoard(), c -> {
            if (c.isBlackHole()) {
                actualBlackHoles.set(actualBlackHoles.get() + 1);
            }
        });
        assertEquals(expectedBlackHoles, actualBlackHoles.get());
        assertEquals(expectedBlackHoles, game.getBlackHolesNumber());
    }

    @Test
    @DisplayName("Exception is thrown if insufficient black holes")
    void shouldFailPlaceMultipleBlackHolesWhenInsufficientBlackHoles() {
        assertThrows(
                IllegalArgumentException.class,
                () -> gameFactory.createGameWithRandomlyDistributedHoles(BOARD_DIMENSION_SIZE, BOARD_DIMENSION_SIZE, 0),
                "Board should contain at least 1 black hole"
        );
    }

    @Test
    @DisplayName("Exception is thrown if too much black holes")
    void shouldFailPlaceMultipleBlackHolesWhenTooMuchBlackHoles() {
        assertThrows(
                IllegalArgumentException.class,
                () -> gameFactory.createGameWithRandomlyDistributedHoles(BOARD_DIMENSION_SIZE, BOARD_DIMENSION_SIZE, 9),
                "Too much black holes. Board should contain at least 1 cell"
        );
    }
}
