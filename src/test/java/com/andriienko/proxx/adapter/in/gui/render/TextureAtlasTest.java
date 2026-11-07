package com.andriienko.proxx.adapter.in.gui.render;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TextureAtlasTest {

    @Test
    @DisplayName("Every sprite should have its own slot, with none past the end of the sheet")
    void shouldGiveEachSpriteItsOwnIndex() {
        Set<Integer> taken = new HashSet<>();
        for (int frame = 0; frame < CellGeometry.IDLE_ANIMATION_FRAMES; frame++) {
            assertTrue(taken.add(frame), "idle frame " + frame + " collides with another sprite");
        }
        for (int sprite : new int[]{TextureAtlas.OUTLINE, TextureAtlas.INNER_CIRCLE,
                TextureAtlas.DOT, TextureAtlas.MINE, TextureAtlas.FOCUS}) {
            assertTrue(taken.add(sprite), "static sprite " + sprite + " collides with another sprite");
            assertTrue(sprite < TextureAtlas.SPRITE_COUNT, "sprite " + sprite + " is past the sheet");
        }
        for (int count = 1; count <= 8; count++) {
            int sprite = TextureAtlas.number(count);
            assertTrue(taken.add(sprite), "number " + count + " collides with another sprite");
            assertTrue(sprite < TextureAtlas.SPRITE_COUNT, "number " + count + " is past the sheet");
        }
        assertEquals(TextureAtlas.SPRITE_COUNT, taken.size(), "the sheet has unused or missing slots");
    }

    @Test
    @DisplayName("The inner circle should sit between the outline and the dot, as the original composites them")
    void shouldOrderTheStaticSprites() {
        assertEquals(CellGeometry.IDLE_ANIMATION_FRAMES, TextureAtlas.OUTLINE);
        assertEquals(TextureAtlas.OUTLINE + 1, TextureAtlas.INNER_CIRCLE);
        assertEquals(TextureAtlas.INNER_CIRCLE + 1, TextureAtlas.DOT);
    }

    @Test
    @DisplayName("Only counts a cell can actually touch should map to a number sprite")
    void shouldRejectImpossibleCounts() {
        assertThrows(IllegalArgumentException.class, () -> TextureAtlas.number(0));
        assertThrows(IllegalArgumentException.class, () -> TextureAtlas.number(9));
    }

    @Test
    @DisplayName("Idle frames should stay in range and wrap with the loop")
    void shouldWrapIdleFrames() {
        assertEquals(0, TextureAtlas.idleFrame(0, 0));
        assertEquals(0, TextureAtlas.idleFrame(CellGeometry.IDLE_ANIMATION_LENGTH_MS, 0));
        for (long ms = 0; ms < 2L * CellGeometry.IDLE_ANIMATION_LENGTH_MS; ms += 17) {
            int frame = TextureAtlas.idleFrame(ms, 9_999);
            assertTrue(frame >= 0 && frame < CellGeometry.IDLE_ANIMATION_FRAMES, "frame out of range: " + frame);
        }
    }
}
