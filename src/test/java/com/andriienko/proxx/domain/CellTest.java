package com.andriienko.proxx.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CellTest {
    Cell cell;

    @BeforeEach
    void setUp() {
        cell = new Cell(0, 0);
    }

    @Test
    @DisplayName("A cell should have correct initial state after creation")
    void shouldHaveCorrectInitialState() {
        assertEquals(0, cell.getRow());
        assertEquals(0, cell.getColumn());
        assertFalse(cell.isOpened());
        assertFalse(cell.isBlackHole());
        assertEquals(0, cell.getAdjacentBlackHolesCount());
    }

    @DisplayName("A cell should not be empty when increase count of adjacent black holes")
    @Test
    public void shouldNotBeEmptyWhenCountOfAdjBlackHolesIncreases() {
        assertTrue(cell.isEmpty());
        cell.addAdjacentBlackHolesCount();
        assertFalse(cell.isEmpty());
    }

    @Test
    @DisplayName("A cell should be marked as black hole")
    void shouldBeMarkedAsBlackHole() {
        assertFalse(cell.isBlackHole());
        cell.markAsBlackHole();
        assertTrue(cell.isBlackHole());
    }

    @Test
    @DisplayName("A cell should be marked as opened")
    void shouldBeMarkedAsOpened() {
        assertFalse(cell.isOpened());
        cell.markAsOpened();
        assertTrue(cell.isOpened());
    }

    @Test
    @DisplayName("A cell should increase count of adjacent black holes")
    void shouldIncreaseCountOfAdjacentBlackHoles() {
        cell.addAdjacentBlackHolesCount();
        assertEquals(1, cell.getAdjacentBlackHolesCount());
    }

    @Test
    @DisplayName("Exception is thrown if a cell's count of adjacent black holes greater then 8")
    void shouldThrowExceptionWhenCountOfAdjacentBlackHolesOutOfBoundary() {
        for (int i = 0; i < 8; i++) {
            cell.addAdjacentBlackHolesCount();
        }
        assertThrows(
                IllegalStateException.class,
                () -> cell.addAdjacentBlackHolesCount(),
                "Cell could be surrounded only by 0..8 black holes"
        );
    }

}
