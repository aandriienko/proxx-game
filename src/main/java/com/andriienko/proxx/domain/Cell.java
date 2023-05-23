package com.andriienko.proxx.domain;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Class Cell:
 * Represents the state information for a single cell on
 * the proxx board.
 */

@Getter(AccessLevel.PUBLIC)
@EqualsAndHashCode(of = {"row", "column"})
public class Cell {
    private final int row;
    private final int column;
    private boolean isRevealed;
    private boolean isBlackHole;
    private int adjacentBlackHolesCount;

    public Cell(int row, int column) {
        this.row = row;
        this.column = column;
        this.isBlackHole = false;
        this.isRevealed = false;
        this.adjacentBlackHolesCount = 0;
    }

    void addAdjacentBlackHolesCount() {
        if (adjacentBlackHolesCount > 7) {
            throw new IllegalStateException("Cell could be surrounded only by 0..8 black holes");
        }
        adjacentBlackHolesCount++;
    }

    public boolean isEmpty() {
        return adjacentBlackHolesCount == 0;
    }

    void markAsRevealed() {
        isRevealed = true;
    }

    void markAsBlackHole() {
        isBlackHole = true;
    }
}
