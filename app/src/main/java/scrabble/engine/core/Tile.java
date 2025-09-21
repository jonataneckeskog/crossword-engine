package scrabble.engine.core;

public final class Tile {
    private final char letter;
    private final int points;

    Tile(char letter, int points) {
        this.letter = letter;
        this.points = points;
    }

    public char getLetter() {
        return letter;
    }

    public int getPoints() {
        return points;
    }
}
