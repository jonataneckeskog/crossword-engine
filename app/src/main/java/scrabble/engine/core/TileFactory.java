package scrabble.engine.core;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import scrabble.engine.util.BoardConstants;

public final class TileFactory {
    private TileFactory() {
    }

    public static final Map<Character, Tile> TILE_POOL;
    public static final Map<Character, Tile> BLANK_POOL;

    static {
        Map<Character, Tile> tempTilePool = new HashMap<>();
        Map<Character, Tile> tempBlankPool = new HashMap<>();

        for (var entry : BoardConstants.TILE_POINTS.entrySet()) {
            char letter = entry.getKey();
            tempTilePool.put(letter, Tile.createTile(letter, entry.getValue()));

            if (letter != '?') {
                tempBlankPool.put(letter, Tile.createAssigned(letter));
            }
        }

        TILE_POOL = Collections.unmodifiableMap(tempTilePool);
        BLANK_POOL = Collections.unmodifiableMap(tempBlankPool);
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
