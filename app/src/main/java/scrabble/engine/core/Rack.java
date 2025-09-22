package scrabble.engine.core;

import java.util.List;
import java.util.ArrayList;

import scrabble.engine.util.BoardConstants;

public final class Rack {
    private final List<Tile> tiles;
    private static final int RACK_SIZE = BoardConstants.RACK_SIZE;

    private Rack(List<Tile> tiles) {
        this.tiles = tiles;
    }

    public static Rack createFromString(String letters) {
        int length = letters.length();
        if (length > RACK_SIZE) {
            throw new IllegalArgumentException(
                    "Input string should contain a maximum of " + RACK_SIZE + " letters. Containes " + length + ".");
        }

        List<Tile> tiles = new ArrayList<>();

        for (int i = 0; i < length; i++) {
            char letter = letters.charAt(i);
            if (!TileFactory.isValidLetter(letter)) {
                throw new IllegalArgumentException("String contains invalid character '" + letter + "'");
            }

            tiles.add(TileFactory.getLetterTile(letter));
        }

        return new Rack(tiles);
    }

    public Rack removeTiles(String letters) {
        int length = letters.length();
        if (length > tiles.size()) {
            throw new IllegalArgumentException(
                    "Cannot draw " + length + " tiles since rack only contains " + tiles.size() + ".");
        }

        List<Tile> remaining = new ArrayList<>(tiles);

        for (int i = 0; i < length; i++) {
            char letter = letters.charAt(i);
            if (!TileFactory.isValidLetter(letter)) {
                throw new IllegalArgumentException("String contains invalid character '" + letter + "'");
            }

            Tile tile = TileFactory.getLetterTile(letter);

            boolean removed = remaining.remove(tile);
            if (!removed) {
                throw new IllegalArgumentException("Cannot draw " + letter + " since it is not on the rack.");
            }
        }

        return new Rack(remaining);
    }

    public Rack addTiles(List<Tile> tiles) {
        List<Tile> newTiles = new ArrayList<>(this.tiles);
        newTiles.addAll(tiles);

        if (newTiles.size() > RACK_SIZE)
            throw new IllegalArgumentException(
                    "Added too many tiles, rack maximum size is " + RACK_SIZE + " but it would have contained "
                            + newTiles.size()
                            + " tiles.");

        return new Rack(newTiles);
    }
}
