package scrabble.engine.core;

import java.util.Map;

public final class Tile {
    private final char letter;
    private final int points;
    private final Character assignedLetter;

    private Tile(char letter, int points) {
        this(letter, points, null);
    }

    private Tile(char letter, int points, Character assignedLetter) {
        this.letter = letter;
        this.points = points;
        this.assignedLetter = assignedLetter;
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
            Map.entry('Y', 4), Map.entry('Z', 10), Map.entry('?', 0));

    public static Tile fromChar(char c) {
        char letter = Character.toUpperCase(c);
        Integer points = TILE_POINTS.get(letter);
        if (points == null) {
            throw new IllegalArgumentException("Letter '" + letter + "' is not valid.");
        }
        return new Tile(letter, points);
    }

    public Tile assignBlankLetter(char letter) {
        if (!isBlank()) {
            throw new IllegalArgumentException("Letter '" + letter + "' is not blank.");
        }
        return new Tile('?', TILE_POINTS.get('?'), letter);
    }

    public static boolean isValidLetter(char letter) {
        return TILE_POINTS.containsKey(Character.toUpperCase(letter));
    }

    public char getLetter() {
        return letter;
    }

    public int getPoints() {
        return points;
    }

    public boolean isBlank() {
        return letter == '?';
    }

    public Character getBlankAssignedLetter() {
        return assignedLetter;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Tile))
            return false;
        Tile other = (Tile) o;
        return letter == other.letter &&
                (assignedLetter == null ? other.assignedLetter == null : assignedLetter.equals(other.assignedLetter));
    }

    @Override
    public int hashCode() {
        return 31 * Character.hashCode(letter) + (assignedLetter == null ? 0 : assignedLetter.hashCode());
    }
}
