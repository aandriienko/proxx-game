package com.andriienko.proxx.adapter.in.gui;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FontsTest {

    @Test
    @DisplayName("Should take the first preferred family the platform actually has")
    void shouldHonourPreferenceOrder() {
        assertEquals("Menlo", Fonts.pickFamily(Set.of("Menlo", "Consolas", "DejaVu Sans Mono", "Arial")));
        assertEquals("Consolas", Fonts.pickFamily(Set.of("Consolas", "DejaVu Sans Mono", "Arial")));
        assertEquals("DejaVu Sans Mono", Fonts.pickFamily(Set.of("DejaVu Sans Mono", "Arial")));
    }

    @Test
    @DisplayName("Should fall back to the logical family when no preferred face is installed")
    void shouldFallBackToLogicalFamily() {
        assertEquals("Monospaced", Fonts.pickFamily(Set.of("Arial", "Times New Roman")));
        assertEquals("Monospaced", Fonts.pickFamily(List.of()));
    }

    @Test
    @DisplayName("Should not be fooled by a near-miss family name")
    void shouldMatchExactly() {
        assertEquals("Monospaced", Fonts.pickFamily(Set.of("Menlo Bold", "Consolas Mono")));
    }
}
