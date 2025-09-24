package scrabble.engine.core.components;

import scrabble.engine.util.BoardConstants;

public final class BlankTile implements Tile {
    private final char letter;
    private final int points;

    private BlankTile(char assignedLetter) {
        this.letter = assignedLetter;
        this.points = BoardConstants.TILE_POINTS.getOrDefault('?', 0);
    }

    public static BlankTile of(char letter) {
        return new BlankTile(letter);
    }

    public boolean isAssigned() {
        return letter != '?';
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
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof BlankTile))
            return false;
        BlankTile that = (BlankTile) o;
        return this.letter == that.letter && this.points == that.points;
    }

    @Override
    public int hashCode() {
        return Character.hashCode(letter) * 31 + Integer.hashCode(points);
    }

}
