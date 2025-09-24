package scrabble.engine.util;

import java.util.Map;

import org.junit.jupiter.api.Test;

import scrabble.engine.util.game.BoardConstants;

import static org.junit.jupiter.api.Assertions.*;

public class BoardConstantsTest {
    @Test
    void testBasicConstants() {
        assertTrue(BoardConstants.SIZE >= 0,
                "The size of the board must be greater than 0. Is: " + BoardConstants.SIZE + ".");
        assertTrue(BoardConstants.TILE_COUNT >= 0,
                "Total number of tiles must best greater than 0. Is: " + BoardConstants.TILE_COUNT + ".");
    }

    @Test
    void testTileCounts() {
        for (Map.Entry<Character, Integer> entry : BoardConstants.TILE_COUNTS.entrySet()) {
            assertNotNull(entry.getKey(), "null key");
            assertNotNull(entry.getValue(), "null points for key " + entry.getKey());
        }
    }

    @Test
    void testTilePoints() {
        for (Map.Entry<Character, Integer> entry : BoardConstants.TILE_POINTS.entrySet()) {
            assertNotNull(entry.getKey(), "null key");
            assertNotNull(entry.getValue(), "null points for key " + entry.getKey());
        }
    }
}