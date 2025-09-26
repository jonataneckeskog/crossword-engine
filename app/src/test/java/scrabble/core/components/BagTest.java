package scrabble.core.components;

import org.junit.jupiter.api.Test;

import scrabble.rules.game.BagConstants;

import static org.junit.jupiter.api.Assertions.*;

public class BagTest {

    private static final String BAG_STRING_1 = "ABCDE";
    private static final String BAG_STRING_2 = "ABCDEFG";
    private static final String BAG_STRING_3 = "A".repeat(BagConstants.TILE_COUNT + 2);
    private static final String BAG_STRING_4 = "--42!--";
    private static final String BAG_STRING_5 = "";
    private static final String BAG_STRING_6 = "ABC";

    @Test
    void testFromString() {
        Bag bag1 = Bag.fromString(BAG_STRING_1);
        Bag bag2 = Bag.fromString(BAG_STRING_1);
        Bag bag3 = Bag.fromString(BAG_STRING_2);

        assertEquals(bag1, bag2);
        assertNotEquals(bag1, bag3);

        assertThrows(IllegalArgumentException.class, () -> Bag.fromString(BAG_STRING_3));
        assertThrows(IllegalArgumentException.class, () -> Bag.fromString(BAG_STRING_4));
        assertDoesNotThrow(() -> Bag.fromString(BAG_STRING_5));

        assertThrows(IllegalArgumentException.class, () -> {
            Bag.fromString("abc"); // Lower case represents blanks in other parts of the project,
            // so bag should be forced to use Upper Case only.
        });
    }

    @Test
    void testDrawTiles() {
        Bag bag = Bag.fromString(BAG_STRING_1);
        int originalSize = bag.size();

        // Draw some tiles
        int drawCount = 3;
        DrawResult result = bag.drawTiles(drawCount);

        assertEquals(originalSize - drawCount, result.bag().size());
        assertEquals(drawCount, result.drawnTiles().length);

        // All drawn tiles should be valid
        for (char t : result.drawnTiles()) {
            assertTrue(BAG_STRING_1.indexOf(t) >= 0, "Drawn tile must be in original bag");
        }

        // Drawing 0 tiles should return identical bag
        DrawResult zeroDraw = bag.drawTiles(0);
        assertEquals(bag, zeroDraw.bag());
        assertEquals(0, zeroDraw.drawnTiles().length);

        // Drawing more than available should throw
        assertThrows(IllegalArgumentException.class, () -> bag.drawTiles(originalSize + 1));
    }

    @Test
    void testRemoveTiles() {
        Bag bag = Bag.fromString("AABBCC");
        Bag bagAfter = bag.removeTiles(new char[] { 'A', 'B' });
        assertEquals(4, bagAfter.size());

        // Removing unavailable tile should throw
        assertThrows(IllegalStateException.class, () -> bagAfter.removeTiles(new char[] { 'Z' }));
    }

    @Test
    void testStandardBag() {
        Bag standard = Bag.standardBag();
        int totalCount = 0;
        for (byte b : standard.getFrequencyMap()) {
            totalCount += b & 0xFF; // convert to unsigned
        }
        assertEquals(BagConstants.TILE_COUNT, totalCount);
        assertFalse(standard.isEmpty());
    }

    @Test
    void testEqualsAndHashCode() {
        Bag bag1 = Bag.fromString(BAG_STRING_1);
        Bag bag2 = Bag.fromString(BAG_STRING_1);
        Bag bag3 = Bag.fromString("ABCD");

        assertEquals(bag1, bag2);
        assertEquals(bag1.hashCode(), bag2.hashCode());
        assertNotEquals(bag1, bag3);
    }

    @Test
    void testToString() {
        Bag bag = Bag.fromString(BAG_STRING_6);
        String str = bag.toString();
        assertTrue(str.contains("A"));
        assertTrue(str.contains("B"));
        assertTrue(str.contains("C"));
    }

    @Test
    void testIsEmptyAndSize() {
        Bag emptyBag = Bag.fromString("");
        assertTrue(emptyBag.isEmpty());
        assertEquals(0, emptyBag.size());

        Bag bag = Bag.fromString(BAG_STRING_6);
        assertFalse(bag.isEmpty());
        assertEquals(3, bag.size());
    }
}