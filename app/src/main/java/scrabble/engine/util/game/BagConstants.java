package scrabble.engine.util.game;

import java.util.Map;

public final class BagConstants {
    private BagConstants() {
    }

    public static final Map<Character, LetterData> TILE_DATA = Map.ofEntries(
            Map.entry('A', new LetterData(9, 1)), Map.entry('B', new LetterData(2, 3)),
            Map.entry('C', new LetterData(2, 3)), Map.entry('D', new LetterData(4, 2)),
            Map.entry('E', new LetterData(12, 1)), Map.entry('F', new LetterData(2, 4)),
            Map.entry('G', new LetterData(3, 2)), Map.entry('H', new LetterData(2, 4)),
            Map.entry('I', new LetterData(9, 1)), Map.entry('J', new LetterData(1, 8)),
            Map.entry('K', new LetterData(1, 5)), Map.entry('L', new LetterData(4, 1)),
            Map.entry('M', new LetterData(2, 3)), Map.entry('N', new LetterData(6, 1)),
            Map.entry('O', new LetterData(8, 1)), Map.entry('P', new LetterData(2, 3)),
            Map.entry('Q', new LetterData(1, 10)), Map.entry('R', new LetterData(6, 1)),
            Map.entry('S', new LetterData(4, 1)), Map.entry('T', new LetterData(6, 1)),
            Map.entry('U', new LetterData(4, 1)), Map.entry('V', new LetterData(2, 4)),
            Map.entry('W', new LetterData(2, 4)), Map.entry('X', new LetterData(1, 8)),
            Map.entry('Y', new LetterData(2, 4)), Map.entry('Z', new LetterData(1, 10)),
            Map.entry('?', new LetterData(2, 0)));

    public static final int UNIQUE_TILES = TILE_DATA.size();
    public static final int TILE_COUNT = TILE_DATA.values().stream().mapToInt(LetterData::count).sum();

    // Precompute mapping: character -> array index
    private static final Map<Character, Integer> TILE_INDEX;
    static {
        TILE_INDEX = new java.util.HashMap<>();
        int index = 0;
        for (Character letter : TILE_DATA.keySet()) {
            TILE_INDEX.put(letter, index++);
        }
    }

    // Precompute reverse: index -> character
    public static final char[] INDEX_TO_CHAR;
    static {
        INDEX_TO_CHAR = new char[TILE_DATA.size()];
        for (Map.Entry<Character, Integer> entry : TILE_INDEX.entrySet()) {
            INDEX_TO_CHAR[entry.getValue()] = entry.getKey();
        }
    }

    public static int getIndex(char letter) {
        return TILE_INDEX.get(letter);
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
}