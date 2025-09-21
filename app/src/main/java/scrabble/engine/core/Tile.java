package scrabble.engine.core;

import java.util.Map;

public final class Tile {
    private final char letter;
    private final int points;

    private Tile(char letter, int points) {
        this.letter = letter;
        this.points = points;
    }

    private static final Map<Character, Integer> TILE_POINTS = Map.ofEntries(
            Map.entry('A', 1), Map.entry('B', 3), Map.entry('C', 3),
            Map.entry('D', 2), Map.entry('E', 1), Map.entry('F', 4),
            Map.entry('G', 2), Map.entry('H', 4), Map.entry('I', 1),
            Map.entry('J', 8), Map.entry('K', 5), Map.entry('L', 1),
            Map.entry('M', 3), Map.entry('N', 1), Map.entry('O', 1),
            Map.entry('P', 3), Map.entry('Q', 10), Map.entry('R', 1),
            Map.entry('S', 1), Map.entry('T', 1), Map.entry('U', 1),
            Map.entry('V', 4), Map.entry('W', 4), Map.entry('X', 8),
            Map.entry('Y', 4), Map.entry('Z', 10), Map.entry('_', 0));

    public static Tile fromChar(char c) {
        char letter = Character.toUpperCase(c);
        Integer pts = TILE_POINTS.get(letter);
        if (pts == null) {
            throw new IllegalArgumentException("Letter '" + c + "' is not valid.");
        }
        return new Tile(letter, pts);
    }

    public static boolean isValidLetter(char letter) {
        return TILE_POINTS.containsKey(letter);
    }

    public char getLetter() {
        return letter;
    }

    public int getPoints() {
        return points;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Tile))
            return false;
        Tile other = (Tile) o;
        return this.letter == other.letter;
    }
}
