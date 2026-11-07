package com.andriienko.proxx.adapter.in.gui.render;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CellGeometry {

    public static final double BORDER_RADIUS = 76.0 / 650.0;
    public static final double THICK_LINE = 20.0 / 650.0;
    public static final double THIN_LINE = 12.0 / 650.0;
    public static final double FLAG_CIRCLE_RADIUS = 32.0 / 650.0;
    public static final double INNER_CIRCLE_RADIUS = 64.0 / 650.0;
    public static final double GLOW_FACTOR = 1.0 / 50.0;
    public static final double GLOW_ALPHA = 0.5;
    public static final double REVEALED_ALPHA = 0.3;
    public static final double FADED_LINES_ALPHA = 0.3;
    public static final double SAFETY_BUFFER_FACTOR = 0.97;

    public static final double NUMBER_CIRCLE_RADIUS = 0.8;
    public static final double NUMBER_FONT_SIZE_FACTOR = 0.45;
    public static final double NUMBER_FONT_TOP_SHIFT_FACTOR = 0.03;

    public static final double BLACK_HOLE_OUTER_RADIUS = 1.0;
    public static final double BLACK_HOLE_INNER_RADIUS = 0.9;
    public static final double BLACK_HOLE_RADIUS = 0.75;

    public static final int NUM_INNER_RECTS = 5;
    public static final int IDLE_ANIMATION_FRAMES = 300;
    public static final int IDLE_ANIMATION_LENGTH_MS = 5000;
    public static final int FADE_IN_MS = 300;
    public static final int FADE_OUT_MS = 300;
    public static final int FLASH_IN_MS = 50;
    public static final int FLASH_OUT_MS = 300;

    public static final int RIPPLE_SPEED_MS = 375;
    public static final int IDLE_RIPPLE_SPEED_MS = 2 * RIPPLE_SPEED_MS;

    public static final int REVEAL_RIPPLE_SPEED_MS = 0;

    public static final double IDLE_RECT_PULSE_OFFSET = 0.14;

    public static final double CELL_GAP = 0.2;

    public static final double MIN_CELL_SIZE = 18.0;
    public static final double MAX_CELL_SIZE = 40.0;

    public static double idleRectPulse(int index, double ts) {
        double offset = ((double) (NUM_INNER_RECTS - index) / NUM_INNER_RECTS) * IDLE_RECT_PULSE_OFFSET;
        return Easings.smoothpulse(0.0, 0.5 + offset, 0.5 + offset, 1.0, ts);
    }

    public static double idleRectAngleDegrees(int index, double ts) {
        return 5.0 + index * 9.0 + index * (index + 1) / 2.0 + idleRectPulse(index, ts) * 180.0;
    }

    public static double idleRectSize(int index, double size, double magnification) {
        return size * magnification * (0.92 - 0.13 * index);
    }

    public static double magnification(double pulse) {
        return Easings.remap(0.0, 1.0, 1.0, 1.4, pulse);
    }

    public static double distanceFromCentre(int row, int column, int rows, int columns) {
        double centreX = columns / 2.0;
        double centreY = rows / 2.0;
        double dx = column + 0.5 - centreX;
        double dy = row + 0.5 - centreY;
        return Math.sqrt(dx * dx + dy * dy) / Math.sqrt(centreX * centreX + centreY * centreY);
    }

    public static double idleRippleSpreadMs(int rows, int columns) {
        return (double) IDLE_RIPPLE_SPEED_MS * Math.max(rows, columns);
    }

    public static double drawableCellSize(double cellSize) {
        return cellSize * (1.0 - CELL_GAP);
    }

    public static double cellInset(double cellSize) {
        return cellSize * CELL_GAP / 2.0;
    }

    public static double fitCellSize(double viewportWidth, double viewportHeight, int rows, int columns) {
        double fitted = Math.min(viewportWidth / columns, viewportHeight / rows);
        return Math.max(MIN_CELL_SIZE, Math.min(MAX_CELL_SIZE, fitted));
    }
}
