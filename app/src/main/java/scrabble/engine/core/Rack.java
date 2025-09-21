package scrabble.engine.core;

import java.util.List;
import java.util.ArrayList;

public final class Rack {
    private final List<Tile> tiles;

    private Rack(List<Tile> tiles) {
        this.tiles = new ArrayList<>(tiles);
    }

    public static Rack createFromString(String letters) {
        int length = letters.length();
        if (length < 8) {
            throw new IllegalArgumentException(
                    "Input string should contain a maximum of 8 letters. Contained " + length + ".");
        }

        List<Tile> tiles = new ArrayList<>();

        for (int i = 0; i < length; i++) {
            char letter = letters.charAt(i);
            if (!Tile.isValidLetter(letter)) {
                throw new IllegalArgumentException("String contains invalid character '" + letter + "'");
            }

            tiles.add(Tile.fromChar(letter));
        }

        return new Rack(tiles);
    }

    public Rack drawTiles(String letters) {
        int length = letters.length();
        if (length > tiles.size()) {
            throw new IllegalArgumentException(
                    "Cannot draw " + length + " tiles since rack only contains " + tiles.size() + ".");
        }

        List<Tile> remaining = new ArrayList<>(tiles);

        for (int i = 0; i < length; i++) {
            char letter = letters.charAt(i);
            if (!Tile.isValidLetter(letter)) {
                throw new IllegalArgumentException("String contains invalid character '" + letter + "'");
            }

            Tile tile = Tile.fromChar(letter);

            boolean removed = remaining.remove(tile);
            if (!removed) {
                throw new IllegalArgumentException("Cannot draw " + letter + " since it is not on the rack.");
            }
        }

        return new Rack(remaining);
    }
}
