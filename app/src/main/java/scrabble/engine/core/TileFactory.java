package scrabble.engine.core;

import java.util.HashMap;
import java.util.Map;

import scrabble.engine.util.BoardConstants;

public final class TileFactory {
    private TileFactory() {
    }

    public static final Map<Character, Tile> TILE_POOL = new HashMap<>();
    public static final Map<Character, Tile> BLANK_POOL = new HashMap<>();

    static {
        for (var entry : BoardConstants.TILE_POINTS.entrySet()) {
            char letter = entry.getKey();

            TILE_POOL.put(letter, Tile.createTile(letter, entry.getValue()));

            if (letter != '?') {
                BLANK_POOL.put(letter, Tile.createAssigned(letter));
            }
        }
    }

    public static boolean isValidLetter(char letter) {
        return BoardConstants.TILE_POINTS.containsKey(letter);
    }

    // Can return an unassigned blank
    public static Tile getTile(char letter) {
        return TILE_POOL.get(letter);
    }

    // Returns assigned blanks
    public static Tile getBlank(char letter) {
        return BLANK_POOL.get(letter);
    }
}
