package scrabble.engine.core;

import scrabble.engine.util.BoardConstants;

public final class BlankTile implements Tile {
    private final char letter;
    private final int points = BoardConstants.TILE_POINTS.get('?');

    private BlankTile() {
        letter = '?';
    }

    private BlankTile(Character assignedLetter) {
        char letter = Character.toUpperCase(assignedLetter);
        this.letter = letter;
    }

    public static BlankTile unassigned() {
        return new BlankTile();
    }

    public static BlankTile assigned(char letter) {
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

}
