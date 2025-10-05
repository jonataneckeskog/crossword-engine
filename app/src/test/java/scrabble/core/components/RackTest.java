package scrabble.core.components;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import scrabble.rules.game.GameConstants;
import scrabble.rules.game.GameRules;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;

public class RackTest {

    @BeforeAll
    static void setup() {
        GameRules.load("src/main/resources/scrabble.json"); // or correct path
    }

    private static final String RACK_STRING_EMPTY = "";
    private static final String RACK_STRING_SIMPLE = "ABCDE";
    private static final String RACK_STRING_LONG = "A".repeat(GameConstants.RACK_SIZE + 1);
    private static final String RACK_STRING_INVALID = "ABcD1!"; // lowercase and invalid chars

    @Test
    void testFromString() {
        // Normal racks
        Rack rack1 = Rack.fromString(RACK_STRING_SIMPLE);
        Rack rack2 = Rack.fromString(RACK_STRING_SIMPLE);
        Rack rack3 = Rack.fromString("ABCDEF");

        assertEquals(rack1, rack2);
        assertNotEquals(rack1, rack3);

        // Too long rack should throw
        assertThrows(IllegalArgumentException.class, () -> Rack.fromString(RACK_STRING_LONG));

        // Invalid characters (lowercase, symbols, digits) should throw
        assertThrows(IllegalArgumentException.class, () -> Rack.fromString(RACK_STRING_INVALID));

        // Empty rack should succeed
        assertDoesNotThrow(() -> Rack.fromString(RACK_STRING_EMPTY));
    }

    @Test
    void testAddTiles() {
        Rack rack = Rack.fromString("ABC");
        char[] toAdd = { 'D', 'E' };
        Rack newRack = rack.addTiles(toAdd);
        assertEquals(5, newRack.size());

        // Adding too many tiles should throw
        char[] tooMany = new char[GameConstants.RACK_SIZE + 1];
        Arrays.fill(tooMany, 'A');
        assertThrows(IllegalArgumentException.class, () -> rack.addTiles(tooMany));
    }

    @Test
    void testRemoveTiles() {
        Rack rack = Rack.fromString("ABCDE");

        Rack afterRemove = rack.removeTiles(new char[] { 'B', 'C' });
        assertEquals(3, afterRemove.size());

        Rack emptyRack = rack.removeTiles(new char[] { 'A', 'B', 'C', 'D', 'E' });
        assertTrue(emptyRack.isEmpty());

        // Removing tile not present should throw
        assertThrows(IllegalStateException.class, () -> afterRemove.removeTiles(new char[] { 'X' }));

        // Removing more than available should throw
        assertThrows(IllegalStateException.class, () -> afterRemove.removeTiles(new char[] { 'B' }));
    }

    @Test
    void testDrawFromBag() {
        Bag bag = Bag.fromString("ABCDEF");
        Rack rack = Rack.fromString("AB");

        DrawHandler handler = rack.drawFrom(bag);
        Rack newRack = handler.rack();
        Bag newBag = handler.bag();

        // Rack should not exceed max size
        assertTrue(newRack.size() <= GameConstants.RACK_SIZE);

        // Bag size should decrease correctly
        assertEquals(bag.size() - (newRack.size() - rack.size()), newBag.size());

        // Frequency map must be valid
        for (byte b : newRack.getFrequencyMap()) {
            assertTrue(b >= 0);
        }
    }

    @Test
    void testSizeAndIsEmpty() {
        Rack emptyRack = Rack.emptyRack();
        assertEquals(0, emptyRack.size());
        assertTrue(emptyRack.isEmpty());

        Rack rack = Rack.fromString("ABC");
        assertEquals(3, rack.size());
        assertFalse(rack.isEmpty());
    }

    @Test
    void testEqualsAndHashCode() {
        Rack rack1 = Rack.fromString("ABC");
        Rack rack2 = Rack.fromString("ABC");
        Rack rack3 = Rack.fromString("ABD");

        assertEquals(rack1, rack2);
        assertEquals(rack1.hashCode(), rack2.hashCode());
        assertNotEquals(rack1, rack3);
    }

    @Test
    void testToString() {
        Rack rack = Rack.fromString("ABC");
        String str = rack.toString();
        assertTrue(str.contains("A"));
        assertTrue(str.contains("B"));
        assertTrue(str.contains("C"));
    }
}