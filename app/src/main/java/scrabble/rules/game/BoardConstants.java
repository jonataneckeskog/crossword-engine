package scrabble.rules.game;

public final class BoardConstants {
        private BoardConstants() {
        }

        public static final int SIZE = 15;
        public static final int TOTAL_SIZE = SIZE * SIZE;

        public static final byte NORMAL = 0;
        public static final byte DOUBLE_LETTER = 1;
        public static final byte TRIPLE_LETTER = 2;
        public static final byte DOUBLE_WORD = 3;
        public static final byte TRIPLE_WORD = 4;

        public static final byte QUADRUPLE_LETTER = 5;
        public static final byte QUADRUPLE_WORD = 6;

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

        public static boolean isBlank(char letter) {
                return !Character.isUpperCase(letter);
        }
}