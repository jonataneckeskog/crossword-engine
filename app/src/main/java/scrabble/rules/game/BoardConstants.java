package scrabble.rules.game;

public final class BoardConstants {
    private BoardConstants() {
    }

    public static int SIZE;
    public static int TOTAL_SIZE;

    public static final byte NORMAL = 0;
    public static final byte DOUBLE_LETTER = 1;
    public static final byte TRIPLE_LETTER = 2;
    public static final byte DOUBLE_WORD = 3;
    public static final byte TRIPLE_WORD = 4;
    public static final byte QUADRUPLE_LETTER = 5;
    public static final byte QUADRUPLE_WORD = 6;

    public static byte[] SCRABBLE_BOARD;

    private static boolean initialized = false;

    public static void initialize(BoardData data) {
        if (initialized)
            return;

        SIZE = data.SIZE;
        TOTAL_SIZE = data.TOTAL_SIZE;

        SCRABBLE_BOARD = data.BOARD_BONUSES;

        initialized = true;
    }

    public static boolean isBlank(char letter) {
        return !Character.isUpperCase(letter);
    }

    /** Helper classes to represent JSON structure */
    public static class BoardData {
        public int SIZE;
        public int TOTAL_SIZE;
        public byte[] BOARD_BONUSES;
    }
}