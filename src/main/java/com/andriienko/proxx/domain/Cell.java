package com.andriienko.proxx.domain;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

/**
 * Class Cell:
 * Represents the state information for a single cell on
 * the proxx board.
 */

@Getter(AccessLevel.PUBLIC)
@FieldDefaults(level = AccessLevel.PRIVATE)
@EqualsAndHashCode(of = {"position"})
public class Cell {
    final Position position;
    boolean isOpened;
    boolean isBlackHole;
    int adjacentBlackHolesCount;

    public Cell(int row, int column) {
        this.position = new Position(row, column);
        this.isBlackHole = false;
        this.isOpened = false;
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

    void markAsOpened() {
        isOpened = true;
    }

    void markAsBlackHole() {
        isBlackHole = true;
    }
}
