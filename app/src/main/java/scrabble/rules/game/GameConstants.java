package scrabble.rules.game;

public final class GameConstants {
    private GameConstants() {
    }

    public static char EMPTY_SQUARE;
    public static int BINGO_BONUS;
    public static int RACK_SIZE;

    private static boolean initialized = false;

    public static void initialize(GameData data) {
        if (initialized)
            return;

        EMPTY_SQUARE = data.EMPTY_SQUARE;
        BINGO_BONUS = data.BINGO_BONUS;
        RACK_SIZE = data.RACK_SIZE;

        initialized = true;
    }

    /** Helper class to represent JSON structure */
    public static class GameData {
        public char EMPTY_SQUARE;
        public int BINGO_BONUS;
        public int RACK_SIZE;
    }
}