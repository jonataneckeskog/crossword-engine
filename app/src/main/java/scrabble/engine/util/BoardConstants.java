package scrabble.engine.util;

import java.util.Map;

public final class BoardConstants {
    private BoardConstants() {
    }

    public static final int BINGO_BONUS = 50;

    public static final int RACK_SIZE = 7;
    public static final int TILE_COUNT = 100;

    // Standard Scrabble tile counts
    public static final Map<Character, Integer> TILE_COUNTS = Map.ofEntries(
            Map.entry('A', 9), Map.entry('B', 2), Map.entry('C', 2),
            Map.entry('D', 4), Map.entry('E', 12), Map.entry('F', 2),
            Map.entry('G', 3), Map.entry('H', 2), Map.entry('I', 9),
            Map.entry('J', 1), Map.entry('K', 1), Map.entry('L', 4),
            Map.entry('M', 2), Map.entry('N', 6), Map.entry('O', 8),
            Map.entry('P', 2), Map.entry('Q', 1), Map.entry('R', 6),
            Map.entry('S', 4), Map.entry('T', 6), Map.entry('U', 4),
            Map.entry('V', 2), Map.entry('W', 2), Map.entry('X', 1),
            Map.entry('Y', 2), Map.entry('Z', 1), Map.entry('?', 2));

    // Standard scrabble Tile values
    public static final Map<Character, Integer> TILE_POINTS = Map.ofEntries(
            Map.entry('A', 1), Map.entry('B', 3), Map.entry('C', 3),
            Map.entry('D', 2), Map.entry('E', 1), Map.entry('F', 4),
            Map.entry('G', 2), Map.entry('H', 4), Map.entry('I', 1),
            Map.entry('J', 8), Map.entry('K', 5), Map.entry('L', 1),
            Map.entry('M', 3), Map.entry('N', 1), Map.entry('O', 1),
            Map.entry('P', 3), Map.entry('Q', 10), Map.entry('R', 1),
            Map.entry('S', 1), Map.entry('T', 1), Map.entry('U', 1),
            Map.entry('V', 4), Map.entry('W', 4), Map.entry('X', 8),
            Map.entry('Y', 4), Map.entry('Z', 10), Map.entry('?', 0));

    public static final int SIZE = 15;
    public static final int TOTAL_SIZE = SIZE * SIZE;

    public static final byte NORMAL = 0;
    public static final byte DOUBLE_LETTER = 1;
    public static final byte TRIPLE_LETTER = 2;
    public static final byte DOUBLE_WORD = 3;
    public static final byte TRIPLE_WORD = 4;

    // Standard 15 x 15 Scrabble board
    public static final byte[] SCRABBLE_BOARD = {
            // Row 0
            TRIPLE_WORD, NORMAL, NORMAL, DOUBLE_LETTER, NORMAL, NORMAL, NORMAL, TRIPLE_WORD, NORMAL, NORMAL,
            NORMAL,
            DOUBLE_LETTER, NORMAL, NORMAL, TRIPLE_WORD,
            // Row 1
            NORMAL, DOUBLE_WORD, NORMAL, NORMAL, NORMAL, TRIPLE_LETTER, NORMAL, NORMAL, NORMAL,
            TRIPLE_LETTER, NORMAL,
            NORMAL, NORMAL, DOUBLE_WORD, NORMAL,
            // Row 2
            NORMAL, NORMAL, DOUBLE_WORD, NORMAL, NORMAL, NORMAL, DOUBLE_LETTER, NORMAL, DOUBLE_LETTER,
            NORMAL, NORMAL,
            NORMAL, DOUBLE_WORD, NORMAL, NORMAL,
            // Row 3
            DOUBLE_LETTER, NORMAL, NORMAL, DOUBLE_WORD, NORMAL, NORMAL, NORMAL, DOUBLE_LETTER, NORMAL,
            NORMAL, NORMAL,
            DOUBLE_WORD, NORMAL, NORMAL, DOUBLE_LETTER,
            // Row 4
            NORMAL, NORMAL, NORMAL, NORMAL, DOUBLE_WORD, NORMAL, NORMAL, NORMAL, NORMAL, NORMAL,
            DOUBLE_WORD, NORMAL,
            NORMAL, NORMAL, NORMAL,
            // Row 5
            NORMAL, TRIPLE_LETTER, NORMAL, NORMAL, NORMAL, TRIPLE_LETTER, NORMAL, NORMAL, NORMAL,
            TRIPLE_LETTER, NORMAL,
            NORMAL, NORMAL, TRIPLE_LETTER, NORMAL,
            // Row 6
            NORMAL, NORMAL, DOUBLE_LETTER, NORMAL, NORMAL, NORMAL, DOUBLE_LETTER, NORMAL, DOUBLE_LETTER,
            NORMAL, NORMAL,
            NORMAL, DOUBLE_LETTER, NORMAL, NORMAL,
            // Row 7
            TRIPLE_WORD, NORMAL, NORMAL, DOUBLE_LETTER, NORMAL, NORMAL, NORMAL, DOUBLE_WORD, NORMAL, NORMAL,
            NORMAL,
            DOUBLE_LETTER, NORMAL, NORMAL, TRIPLE_WORD,
            // Row 8
            NORMAL, NORMAL, DOUBLE_LETTER, NORMAL, NORMAL, NORMAL, DOUBLE_LETTER, NORMAL, DOUBLE_LETTER,
            NORMAL, NORMAL,
            NORMAL, DOUBLE_LETTER, NORMAL, NORMAL,
            // Row 9
            NORMAL, TRIPLE_LETTER, NORMAL, NORMAL, NORMAL, TRIPLE_LETTER, NORMAL, NORMAL, NORMAL,
            TRIPLE_LETTER, NORMAL,
            NORMAL, NORMAL, TRIPLE_LETTER, NORMAL,
            // Row 10
            NORMAL, NORMAL, NORMAL, NORMAL, DOUBLE_WORD, NORMAL, NORMAL, NORMAL, NORMAL, NORMAL,
            DOUBLE_WORD, NORMAL,
            NORMAL, NORMAL, NORMAL,
            // Row 11
            DOUBLE_LETTER, NORMAL, NORMAL, DOUBLE_WORD, NORMAL, NORMAL, NORMAL, DOUBLE_LETTER, NORMAL,
            NORMAL, NORMAL,
            DOUBLE_WORD, NORMAL, NORMAL, DOUBLE_LETTER,
            // Row 12
            NORMAL, NORMAL, DOUBLE_WORD, NORMAL, NORMAL, NORMAL, DOUBLE_LETTER, NORMAL, DOUBLE_LETTER,
            NORMAL, NORMAL,
            NORMAL, DOUBLE_WORD, NORMAL, NORMAL,
            // Row 13
            NORMAL, DOUBLE_WORD, NORMAL, NORMAL, NORMAL, TRIPLE_LETTER, NORMAL, NORMAL, NORMAL,
            TRIPLE_LETTER, NORMAL,
            NORMAL, NORMAL, DOUBLE_WORD, NORMAL,
            // Row 14
            TRIPLE_WORD, NORMAL, NORMAL, DOUBLE_LETTER, NORMAL, NORMAL, NORMAL, TRIPLE_WORD, NORMAL, NORMAL,
            NORMAL,
            DOUBLE_LETTER, NORMAL, NORMAL, TRIPLE_WORD
    };
}