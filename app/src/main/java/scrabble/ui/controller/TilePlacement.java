package scrabble.ui.controller;

/** Represents a single tile placed on the board by the player */
public class TilePlacement {
    private final int row;
    private final int col;
    private final char letter;

    public TilePlacement(int row, int col, char letter) {
        this.row = row;
        this.col = col;
        this.letter = letter;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public char getLetter() {
        return letter;
    }

    @Override
    public String toString() {
        return letter + "@" + row + "," + col;
    }
}
