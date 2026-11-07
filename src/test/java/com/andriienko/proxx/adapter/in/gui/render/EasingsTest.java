package com.andriienko.proxx.adapter.in.gui.render;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EasingsTest {

    private static final double EPS = 1e-9;

    @Test
    @DisplayName("Every easing should be pinned at 0 and 1")
    void shouldBePinnedAtEndpoints() {
        assertEquals(0.0, Easings.easeInQuad(0.0), EPS);
        assertEquals(1.0, Easings.easeInQuad(1.0), EPS);
        assertEquals(0.0, Easings.easeOutQuad(0.0), EPS);
        assertEquals(1.0, Easings.easeOutQuad(1.0), EPS);
        assertEquals(0.0, Easings.easeInOutQuad(0.0), EPS);
        assertEquals(1.0, Easings.easeInOutQuad(1.0), EPS);
        assertEquals(0.0, Easings.easeInCubic(0.0), EPS);
        assertEquals(1.0, Easings.easeInCubic(1.0), EPS);
        assertEquals(0.0, Easings.easeOutCubic(0.0), EPS);
        assertEquals(1.0, Easings.easeOutCubic(1.0), EPS);
        assertEquals(0.0, Easings.easeInOutCubic(0.0), EPS);
        assertEquals(1.0, Easings.easeInOutCubic(1.0), EPS);
        assertEquals(0.0, Easings.easeInExpo(0.0), EPS);
        assertEquals(1.0, Easings.easeInExpo(1.0), EPS);
        assertEquals(0.0, Easings.easeOutExpo(0.0), EPS);
        assertEquals(1.0, Easings.easeOutExpo(1.0), EPS);
        assertEquals(0.0, Easings.easeInOutExpo(0.0), EPS);
        assertEquals(1.0, Easings.easeInOutExpo(1.0), EPS);
    }

    @Test
    @DisplayName("Quadratic and cubic easings should match their formulas at the midpoint")
    void shouldMatchFormulasAtMidpoint() {
        assertEquals(0.25, Easings.easeInQuad(0.5), EPS);
        assertEquals(0.75, Easings.easeOutQuad(0.5), EPS);
        assertEquals(0.5, Easings.easeInOutQuad(0.5), EPS);
        assertEquals(0.125, Easings.easeInCubic(0.5), EPS);
        assertEquals(0.875, Easings.easeOutCubic(0.5), EPS);
        assertEquals(0.5, Easings.easeInOutCubic(0.5), EPS);
    }

    @Test
    @DisplayName("clamp should bound values to 0..1")
    void shouldClamp() {
        assertEquals(0.0, Easings.clamp(-3.0), EPS);
        assertEquals(1.0, Easings.clamp(4.0), EPS);
        assertEquals(0.4, Easings.clamp(0.4), EPS);
    }

    @Test
    @DisplayName("remap should map a range linearly and clampedRemap should bound it")
    void shouldRemap() {
        assertEquals(15.0, Easings.remap(0.0, 1.0, 10.0, 20.0, 0.5), EPS);
        assertEquals(30.0, Easings.remap(0.0, 1.0, 10.0, 20.0, 2.0), EPS);
        assertEquals(1.0, Easings.clampedRemap(0.0, 1.0, 10.0, 20.0, 2.0), EPS);
    }

    @Test
    @DisplayName("smoothpulse should rise to 1 and fall back to 0")
    void shouldPulse() {
        assertEquals(0.0, Easings.smoothpulse(0.0, 0.5, 0.5, 1.0, 0.0), EPS);
        assertEquals(1.0, Easings.smoothpulse(0.0, 0.5, 0.5, 1.0, 0.5), EPS);
        assertEquals(0.0, Easings.smoothpulse(0.0, 0.5, 0.5, 1.0, 1.0), EPS);
        assertTrue(Easings.smoothpulse(0.0, 0.5, 0.5, 1.0, 0.25) > 0.0);
        assertTrue(Easings.smoothpulse(0.0, 0.5, 0.5, 1.0, 0.75) > 0.0);
    }

    @Test
    @DisplayName("smoothpulse should rise monotonically over its rising half")
    void shouldRiseMonotonically() {
        double previous = -1.0;
        for (int i = 0; i <= 50; i++) {
            double value = Easings.smoothpulse(0.0, 0.5, 0.5, 1.0, i / 100.0);
            assertTrue(value >= previous, "not monotonic at t=" + (i / 100.0));
            previous = value;
        }
    }
}
