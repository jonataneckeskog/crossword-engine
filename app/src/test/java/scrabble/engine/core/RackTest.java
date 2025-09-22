package scrabble.engine.core;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import scrabble.engine.util.BoardConstants;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RackTest {
    private final static String RACK_STRING_1 = "abcde";
    private final static String RACK_STRING_2 = "abcdefg";
    private final static String RACK_STRING_3 = "a".repeat(BoardConstants.RACK_SIZE + 2);
    private final static String RACK_STRING_4 = "--42!--";
    private final static String RACK_STRING_5 = "";

    private final static Tile TILE_1 = TileFactory.getLetterTile('a');
    private final static Tile TILE_2 = TileFactory.getLetterTile('b');
    private final static Tile TILE_3 = TileFactory.getLetterTile('x');

    @Test
    void testCreateFromString() {
        Rack rack1 = Rack.createFromString(RACK_STRING_1);
        Rack rack2 = Rack.createFromString(RACK_STRING_1);
        Rack rack3 = Rack.createFromString(RACK_STRING_2);

        assertEquals(rack1, rack2);
        assertNotEquals(rack2, rack3);

        assertThrows(IllegalArgumentException.class, () -> {
            Rack.createFromString(RACK_STRING_3);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            Rack.createFromString(RACK_STRING_4);
        });
        assertDoesNotThrow(() -> {
            Rack.createFromString(RACK_STRING_5);
        });
    }

    @Test
    void testGetRacks() {
        Rack rack = Rack.createFromString(RACK_STRING_1);
        List<Tile> tiles = rack.getTiles();
        assertTrue(tiles.contains(TILE_1));
        assertTrue(tiles.contains(TILE_2));
        assertFalse(tiles.contains(TILE_3));
    }

    @Test
    void testLetters() {
        Rack rack = Rack.createFromString(RACK_STRING_1);
        String letters = rack.letters();

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
    void testRemoveTiles() {
        Rack rack = Rack.createFromString(RACK_STRING_1);

        assertEquals(Rack.createFromString("ade"), rack.removeTiles("bc"));
        assertEquals(Rack.createFromString(""), rack.removeTiles("abcde"));

        assertThrows(IllegalArgumentException.class, () -> {
            rack.removeTiles("x");
        });
        assertThrows(IllegalArgumentException.class, () -> {
            rack.removeTiles("aa");
        });
    }

    @Test
    void testAddTiles() {
        Rack rack = Rack.createFromString(RACK_STRING_1);

        List<Tile> tiles = new ArrayList<>();

        tiles.add(TILE_1);
        assertEquals(Rack.createFromString("abcdea"), rack.addTiles(tiles));

        tiles.remove(TILE_1);
        tiles.add(TILE_3);
        tiles.add(TILE_3);
        assertEquals(Rack.createFromString("abcdexx"), rack.addTiles(tiles));

        tiles.add(TILE_3); // Too large
        assertThrows(IllegalArgumentException.class, () -> {
            rack.addTiles(tiles);
        });
    }
}