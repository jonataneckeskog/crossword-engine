package scrabble.engine.core.components;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import scrabble.engine.util.game.BoardConstants;

import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.stream.Stream;

class TileFactoryTest {

    // Provide letters and points from BoardConstants
    static Stream<Map.Entry<Character, Integer>> validLetterProvider() {
        return BoardConstants.TILE_POINTS.entrySet().stream();
    }

    // Provide a stream of invalid letters
    static Stream<Character> invalidLetterProvider() {
        return Stream.of('1', '@', ' ', '\0');
    }

    @ParameterizedTest
    @MethodSource("validLetterProvider")
    void testValidLetter(Map.Entry<Character, Integer> entry) {
        char letter = entry.getKey();
        assertTrue(TileFactory.isValidLetter(letter), "Letter should be valid: " + letter);
    }

    @ParameterizedTest
    @MethodSource("invalidLetterProvider")
    void testInvalidLetter(char letter) {
        assertFalse(TileFactory.isValidLetter(letter),
                "Letter " + letter + " is not valid and should return false.");
    }

    @ParameterizedTest
    @MethodSource("validLetterProvider")
    void testGetTile(Map.Entry<Character, Integer> entry) {
        char letter = entry.getKey();
        Tile tile = TileFactory.getTile(letter);
        assertNotNull(tile, "Tile should not be null for letter: " + letter);
        assertEquals(letter, tile.letter());
        assertEquals(entry.getValue(), tile.points());
        if (letter == '?') {
            assertTrue(tile instanceof BlankTile, "Letter '?' should create a blank tile.");
        } else {
            assertFalse(tile.isBlank(), "Tile should be a LetterTile for letter: " + letter);
            assertTrue(tile instanceof LetterTile, "Tile should be a LetterTile for letter: " + letter);
        }
    }

    @ParameterizedTest
    @MethodSource("validLetterProvider")
    void testGetBlank(Map.Entry<Character, Integer> entry) {
        char letter = entry.getKey();

        if (letter != '?') {
            Tile tile = TileFactory.getBlank(letter);
            assertNotNull(tile, "Blank tile should not be null for letter: " + letter);
            assertEquals(letter, tile.letter());

            assertTrue(tile instanceof BlankTile, "Tile should be a BlankTile");
            BlankTile blankTile = (BlankTile) tile;
            assertTrue(blankTile.isAssigned(), "Blank tile should be assigned");
        }
    }

    @Test
    void testPoolsAreUnmodifiable() {
        assertThrows(UnsupportedOperationException.class,
                () -> TileFactory.TILE_POOL.put('A', Tile.createLetter('A', 1)));
        assertThrows(UnsupportedOperationException.class,
                () -> TileFactory.BLANK_POOL.put('A', Tile.createBlank('A')));
    }
}