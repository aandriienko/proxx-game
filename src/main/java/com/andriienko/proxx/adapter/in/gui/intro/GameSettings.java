package com.andriienko.proxx.adapter.in.gui.intro;

import com.andriienko.proxx.enums.PlayMode;

import java.text.MessageFormat;

public record GameSettings(int rows, int columns, int blackHoles) {

    public static final int MIN_SIDE = 3;
    public static final int MAX_SIDE = 40;

    public GameSettings {
        if (!isValid(rows, columns, blackHoles)) {
            throw new IllegalArgumentException(MessageFormat.format(
                    "Invalid settings: {0}x{1} with {2} black holes. Sides must be {3}..{4} and black holes 1..{5}",
                    rows, columns, blackHoles, MIN_SIDE, MAX_SIDE, maxBlackHoles(rows, columns)));
        }
    }

    public static GameSettings of(PlayMode mode) {
        if (mode == PlayMode.CUSTOM) {
            throw new IllegalArgumentException("CUSTOM carries no dimensions; build GameSettings from the form fields");
        }
        return new GameSettings(mode.getRows(), mode.getColumns(), mode.getBlackHoles());
    }

    public static int maxBlackHoles(int rows, int columns) {
        return rows * columns - 1;
    }

    public static boolean isValid(int rows, int columns, int blackHoles) {
        return rows >= MIN_SIDE && rows <= MAX_SIDE
                && columns >= MIN_SIDE && columns <= MAX_SIDE
                && blackHoles >= 1 && blackHoles <= maxBlackHoles(rows, columns);
    }

    public int size() {
        return rows * columns;
    }
}
