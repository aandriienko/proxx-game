package com.andriienko.proxx.adapter.in.gui.intro;

import com.andriienko.proxx.enums.PlayMode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GameSettingsTest {

    @Test
    @DisplayName("Should expose the dimensions it was built with")
    void shouldHoldItsValues() {
        GameSettings settings = new GameSettings(8, 10, 12);

        assertEquals(8, settings.rows());
        assertEquals(10, settings.columns());
        assertEquals(12, settings.blackHoles());
    }

    @Test
    @DisplayName("Should build settings from each predefined play mode")
    void shouldBuildFromPlayMode() {
        assertEquals(new GameSettings(8, 8, 10), GameSettings.of(PlayMode.EASY));
        assertEquals(new GameSettings(16, 16, 40), GameSettings.of(PlayMode.MEDIUM));
        assertEquals(new GameSettings(24, 24, 99), GameSettings.of(PlayMode.EXPERT));
    }

    @Test
    @DisplayName("Should reject CUSTOM, which carries no dimensions of its own")
    void shouldRejectCustomPlayMode() {
        assertThrows(IllegalArgumentException.class, () -> GameSettings.of(PlayMode.CUSTOM));
    }

    @Test
    @DisplayName("The black hole maximum should leave at least one safe cell")
    void shouldCapBlackHoles() {
        assertEquals(8, GameSettings.maxBlackHoles(3, 3));
        assertEquals(1599, GameSettings.maxBlackHoles(40, 40));
    }

    @Test
    @DisplayName("Should accept dimensions on the boundaries")
    void shouldAcceptBoundaryDimensions() {
        assertTrue(GameSettings.isValid(3, 3, 1));
        assertTrue(GameSettings.isValid(40, 40, 1599));
    }

    @Test
    @DisplayName("Should reject dimensions outside 3..40")
    void shouldRejectOutOfRangeDimensions() {
        assertFalse(GameSettings.isValid(2, 8, 5));
        assertFalse(GameSettings.isValid(8, 2, 5));
        assertFalse(GameSettings.isValid(41, 8, 5));
        assertFalse(GameSettings.isValid(8, 41, 5));
    }

    @Test
    @DisplayName("Should reject a black hole count below one or above the maximum")
    void shouldRejectOutOfRangeBlackHoles() {
        assertFalse(GameSettings.isValid(3, 3, 0));
        assertFalse(GameSettings.isValid(3, 3, 9));
    }

    @Test
    @DisplayName("The constructor should reject what isValid rejects")
    void shouldThrowOnInvalidConstruction() {
        assertThrows(IllegalArgumentException.class, () -> new GameSettings(2, 8, 5));
        assertThrows(IllegalArgumentException.class, () -> new GameSettings(3, 3, 9));
    }

    @Test
    @DisplayName("Should report its total cell count")
    void shouldReportSize() {
        assertEquals(24, new GameSettings(4, 6, 5).size());
    }
}
