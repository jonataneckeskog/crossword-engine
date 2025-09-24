package scrabble.engine.core.components;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import scrabble.engine.util.BoardConstants;

import java.util.List;
import java.util.stream.Collectors;

public class BagTest {
    private final static String BAG_STRING_1 = "abcde";
    private final static String BAG_STRING_2 = "abcdefg";
    private final static String BAG_STRING_3 = "a".repeat(BoardConstants.TILE_COUNT + 2);
    private final static String BAG_STRING_4 = "--42!--";
    private final static String BAG_STRING_5 = "";

    private final static Tile TILE_1 = TileFactory.getTile('a');
    private final static Tile TILE_2 = TileFactory.getTile('b');
    private final static Tile TILE_3 = TileFactory.getTile('x');

    @Test
    void testCreateFromString() {
        Bag bag1 = Bag.createFromString(BAG_STRING_1);
        Bag bag2 = Bag.createFromString(BAG_STRING_1);
        Bag bag3 = Bag.createFromString(BAG_STRING_2);

        assertEquals(bag1, bag2);
        assertNotEquals(bag1, bag3);

        assertThrows(IllegalArgumentException.class, () -> {
            Bag.createFromString(BAG_STRING_3);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            Bag.createFromString(BAG_STRING_4);
        });
        assertDoesNotThrow(() -> {
            Bag.createFromString(BAG_STRING_5);
        });
    }

    @Test
    void testGetBags() {
        Bag bag = Bag.createFromString(BAG_STRING_1);
        List<Tile> tiles = bag.getTiles();
        assertTrue(tiles.contains(TILE_1));
        assertTrue(tiles.contains(TILE_2));
        assertFalse(tiles.contains(TILE_3));
    }

    @Test
    void testLetters() {
        Bag bag = Bag.createFromString(BAG_STRING_1);
        String letters = bag.letters();

        List<Character> expectedList = "ACEDB".chars()
                .mapToObj(c -> (char) c)
                .collect(Collectors.toList());

        for (int i = 0; i < letters.length(); i++) {
            Character letter = letters.charAt(i);
            assertTrue(expectedList.remove(letter));
        }

        assertTrue(expectedList.isEmpty());
    }

    @Test
    void testDrawTiles() {
        Bag bag1 = Bag.createFromString(BAG_STRING_1);
        Bag bag2 = Bag.createFromString("hk").drawTiles(1).bag();

        assertEquals(Bag.createFromString(BAG_STRING_5), bag1.drawTiles(5).bag());
        assertEquals(Bag.createFromString(BAG_STRING_1), bag1.drawTiles(0).bag());

        assertTrue(bag2.equals(Bag.createFromString("h")) || bag2.equals(Bag.createFromString("k")));

        assertThrows(IllegalArgumentException.class, () -> {
            bag1.drawTiles(8);
        });
    }
}