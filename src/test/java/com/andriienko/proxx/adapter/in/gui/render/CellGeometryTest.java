package com.andriienko.proxx.adapter.in.gui.render;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CellGeometryTest {

    private static final double EPS = 1e-9;

    @Test
    @DisplayName("Constants should match the original PROXX values")
    void shouldMatchOriginalConstants() {
        assertEquals(76.0 / 650.0, CellGeometry.BORDER_RADIUS, EPS);
        assertEquals(20.0 / 650.0, CellGeometry.THICK_LINE, EPS);
        assertEquals(12.0 / 650.0, CellGeometry.THIN_LINE, EPS);
        assertEquals(32.0 / 650.0, CellGeometry.FLAG_CIRCLE_RADIUS, EPS);
        assertEquals(64.0 / 650.0, CellGeometry.INNER_CIRCLE_RADIUS, EPS);
        assertEquals(0.8, CellGeometry.NUMBER_CIRCLE_RADIUS, EPS);
        assertEquals(0.45, CellGeometry.NUMBER_FONT_SIZE_FACTOR, EPS);
        assertEquals(0.03, CellGeometry.NUMBER_FONT_TOP_SHIFT_FACTOR, EPS);
        assertEquals(1.0 / 50.0, CellGeometry.GLOW_FACTOR, EPS);
        assertEquals(0.5, CellGeometry.GLOW_ALPHA, EPS);
        assertEquals(0.3, CellGeometry.REVEALED_ALPHA, EPS);
        assertEquals(0.3, CellGeometry.FADED_LINES_ALPHA, EPS);
        assertEquals(0.97, CellGeometry.SAFETY_BUFFER_FACTOR, EPS);
        assertEquals(5, CellGeometry.NUM_INNER_RECTS);
        assertEquals(300, CellGeometry.IDLE_ANIMATION_FRAMES);
        assertEquals(5000, CellGeometry.IDLE_ANIMATION_LENGTH_MS);
        assertEquals(375, CellGeometry.RIPPLE_SPEED_MS);
        assertEquals(0.14, CellGeometry.IDLE_RECT_PULSE_OFFSET, EPS);
        assertEquals(1.0, CellGeometry.BLACK_HOLE_OUTER_RADIUS, EPS);
        assertEquals(0.9, CellGeometry.BLACK_HOLE_INNER_RADIUS, EPS);
        assertEquals(0.75, CellGeometry.BLACK_HOLE_RADIUS, EPS);
    }

    @Test
    @DisplayName("The idle ripple should travel at half the original's speed")
    void shouldHalveTheRippleSpeed() {
        assertEquals(2 * CellGeometry.RIPPLE_SPEED_MS, CellGeometry.IDLE_RIPPLE_SPEED_MS);
        // Spread is one ripple period per cell of the longest side, as the original does.
        assertEquals(750.0 * 24, CellGeometry.idleRippleSpreadMs(24, 24), EPS);
        assertEquals(750.0 * 40, CellGeometry.idleRippleSpreadMs(10, 40), EPS);
    }

    @Test
    @DisplayName("Inner rects should shrink as the index grows")
    void shouldShrinkInnerRects() {
        double previous = Double.MAX_VALUE;
        for (int i = 0; i < CellGeometry.NUM_INNER_RECTS; i++) {
            double size = CellGeometry.idleRectSize(i, 100.0, 1.0);
            assertTrue(size < previous, "rect " + i + " is not smaller than its predecessor");
            assertTrue(size > 0.0, "rect " + i + " has a non-positive size");
            previous = size;
        }
        assertEquals(92.0, CellGeometry.idleRectSize(0, 100.0, 1.0), EPS);
        assertEquals(79.0, CellGeometry.idleRectSize(1, 100.0, 1.0), EPS);
    }

    @Test
    @DisplayName("Inner rect angles should differ per index at the start of the loop")
    void shouldGiveEachRectItsOwnAngle() {
        assertEquals(5.0, CellGeometry.idleRectAngleDegrees(0, 0.0), EPS);
        assertEquals(15.0, CellGeometry.idleRectAngleDegrees(1, 0.0), EPS);
        assertEquals(26.0, CellGeometry.idleRectAngleDegrees(2, 0.0), EPS);
        assertEquals(38.0, CellGeometry.idleRectAngleDegrees(3, 0.0), EPS);
        assertEquals(51.0, CellGeometry.idleRectAngleDegrees(4, 0.0), EPS);
    }

    @Test
    @DisplayName("Each rect should reverse at its own point in the loop, inner first")
    void shouldStaggerTheRectReversals() {
        double previousPeak = Double.MAX_VALUE;
        for (int i = 0; i < CellGeometry.NUM_INNER_RECTS; i++) {
            double offset = ((double) (CellGeometry.NUM_INNER_RECTS - i) / CellGeometry.NUM_INNER_RECTS)
                    * CellGeometry.IDLE_RECT_PULSE_OFFSET;
            double peakTs = 0.5 + offset;
            assertEquals(1.0, CellGeometry.idleRectPulse(i, peakTs), EPS, "rect " + i + " does not peak at its own ts");
            assertTrue(peakTs < previousPeak, "rect " + i + " should reverse before rect " + (i - 1));
            previousPeak = peakTs;
        }
        assertEquals(185.0, CellGeometry.idleRectAngleDegrees(0, 0.64), EPS);
        assertEquals(231.0, CellGeometry.idleRectAngleDegrees(4, 0.528), EPS);
    }

    @Test
    @DisplayName("Rects should not all reverse on the same frame")
    void shouldNotReverseInLockstep() {
        double innermost = CellGeometry.idleRectPulse(CellGeometry.NUM_INNER_RECTS - 1, 0.528);
        double outermost = CellGeometry.idleRectPulse(0, 0.528);
        assertEquals(1.0, innermost, EPS);
        assertTrue(outermost < innermost,
                "the outer rect should still be winding up when the inner one reverses");
    }

    @Test
    @DisplayName("The flag dot should sit inside the cell's centre ring")
    void shouldFitTheDotInsideTheInnerCircle() {
        assertTrue(CellGeometry.FLAG_CIRCLE_RADIUS < CellGeometry.INNER_CIRCLE_RADIUS,
                "the dot must be smaller than the ring it lands in");
        assertTrue(CellGeometry.INNER_CIRCLE_RADIUS < 0.5 - CellGeometry.THICK_LINE);
    }

    @Test
    @DisplayName("Magnification should run from 1.0 to 1.4")
    void shouldMagnify() {
        assertEquals(1.0, CellGeometry.magnification(0.0), EPS);
        assertEquals(1.4, CellGeometry.magnification(1.0), EPS);
    }

    @Test
    @DisplayName("Ripple distance should be 0 at the board centre and peak at the corners")
    void shouldMeasureDistanceFromTheCentre() {
        assertEquals(0.875, CellGeometry.distanceFromCentre(0, 0, 8, 8), EPS);
        assertEquals(0.875, CellGeometry.distanceFromCentre(7, 7, 8, 8), EPS);
        assertEquals(0.875, CellGeometry.distanceFromCentre(0, 7, 8, 8), EPS);
        for (int row = 0; row < 8; row++) {
            for (int column = 0; column < 8; column++) {
                assertTrue(CellGeometry.distanceFromCentre(row, column, 8, 8) <= 0.875 + EPS,
                        "cell " + row + "," + column + " is further out than a corner");
            }
        }
        double middle = CellGeometry.distanceFromCentre(3, 3, 8, 8);
        assertTrue(middle < 0.2, "centre cell should be near 0, was " + middle);
        assertEquals(0.0, CellGeometry.distanceFromCentre(4, 4, 9, 9), EPS);
    }

    @Test
    @DisplayName("Distance should grow monotonically as cells move away from the centre")
    void shouldGrowAwayFromTheCentre() {
        double previous = -1.0;
        for (int row = 4; row >= 0; row--) {
            double d = CellGeometry.distanceFromCentre(row, 4, 9, 9);
            assertTrue(d > previous, "row " + row + " is not further out than the one before it");
            previous = d;
        }
    }

    @Test
    @DisplayName("Cells should be drawn inset so a gap shows between them")
    void shouldLeaveAGapBetweenCells() {
        assertEquals(0.2, CellGeometry.CELL_GAP, EPS);
        assertEquals(80.0, CellGeometry.drawableCellSize(100.0), EPS);
        assertEquals(10.0, CellGeometry.cellInset(100.0), EPS);
        assertTrue(CellGeometry.drawableCellSize(18.0) < 18.0);
    }

    @Test
    @DisplayName("Largest cell size that fits the given viewport, clamped to the playable range")
    void shouldFitCellSize() {
        assertEquals(40.0, CellGeometry.fitCellSize(900.0, 900.0, 8, 8), EPS);
        assertEquals(18.0, CellGeometry.fitCellSize(100.0, 100.0, 40, 40), EPS);
        assertEquals(25.0, CellGeometry.fitCellSize(600.0, 600.0, 24, 24), EPS);
    }
}
