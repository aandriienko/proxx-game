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
@EqualsAndHashCode(of = {"row", "column"})
public class Cell {
    final int row;
    final int column;
    boolean isOpened;
    boolean isBlackHole;
    int adjacentBlackHolesCount;

    //todo: private constructor + factory methods: for bh and regular, bh - final
    public Cell(int row, int column) {
        this.row = row;
        this.column = column;
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
