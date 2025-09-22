package scrabble.engine.core;

public final class LetterTile implements Tile {
    private final char letter;
    private final int points;

    private LetterTile(char letter, int points) {
        this.letter = letter;
        this.points = points;
    }

    public static LetterTile of(char letter, int points) {
        return new LetterTile(letter, points);
    }

    @Override
    public char letter() {
        return letter;
    }

    @Override
    public int points() {
        return points;
    }

    @Override
    public boolean isBlank() {
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof LetterTile))
            return false;
        LetterTile that = (LetterTile) o;
        return letter == that.letter && points == that.points;
    }

    @Override
    public int hashCode() {
        return Character.hashCode(letter) * 31 + Integer.hashCode(points);
    }
}
