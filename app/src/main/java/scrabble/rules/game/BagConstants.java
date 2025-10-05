package scrabble.rules.game;

import java.util.HashMap;
import java.util.Map;

public final class BagConstants {
    private BagConstants() {
    }

    public static Map<Character, LetterData> TILE_DATA;
    public static char BLANK;
    public static int UNIQUE_TILES;
    public static int TILE_COUNT;
    public static Map<Character, Integer> TILE_INDEX;
    public static char[] INDEX_TO_CHAR;

    private static boolean initialized = false;

    public static void initialize(BagData bagData) {
        if (initialized)
            return;

        TILE_DATA = Map.copyOf(bagData.TILE_DATA);
        BLANK = bagData.BLANK;

        UNIQUE_TILES = TILE_DATA.size();
        TILE_COUNT = TILE_DATA.values().stream().mapToInt(LetterData::count).sum();

        INDEX_TO_CHAR = new char[TILE_DATA.size()];
        TILE_INDEX = new HashMap<>();
        int i = 0;
        for (Character c : TILE_DATA.keySet()) {
            INDEX_TO_CHAR[i] = c;
            TILE_INDEX.put(c, i++);
        }

        initialized = true;
    }

    public static int getIndex(char letter) {
        Integer idx = TILE_INDEX.get(Character.toUpperCase(letter));
        if (idx == null)
            throw new IllegalArgumentException("Invalid tile: " + letter);
        return idx;
    }

    public static boolean isValidLetter(char letter) {
        return TILE_DATA.containsKey(letter);
    }

    public record LetterData(int count, int score) {
        public int getCount() {
            return count;
        }

        public int getScore() {
            return score;
        }
    }

    public static class BagData {
        public Map<Character, LetterData> TILE_DATA;
        public char BLANK;

        public static class TileData {
            public int count;
            public int score;
        }
    }
}
