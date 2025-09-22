package scrabble.engine.core;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import scrabble.engine.util.BoardConstants;

public final class Bag {
    private final List<Tile> tiles;

    private Bag(List<Tile> tiles) {
        this.tiles = tiles;
    }

    public static Bag standardBag() {
        List<Tile> tiles = new ArrayList<>();

        for (var entry : BoardConstants.TILE_COUNTS.entrySet()) {
            char letter = entry.getKey();
            int count = entry.getValue();

            for (int i = 0; i < count; i++) {
                tiles.add(TileFactory.getLetterTile(letter));
            }
        }

        return new Bag(tiles);
    }

    public static Bag createFromString(String letters) {
        int length = letters.length();
        if (length > BoardConstants.TILE_COUNT) {
            throw new IllegalArgumentException(
                    "String cannot contain more than " + BoardConstants.TILE_COUNT + " letters. It currently contains "
                            + length + ".");
        }

        List<Tile> tiles = new ArrayList<>();

        for (int i = 0; i < length; i++) {
            char letter = letters.charAt(i);
            if (!TileFactory.isValidLetter(letter)) {
                throw new IllegalArgumentException("String contains invalid character '" + letter + "'");
            }

            tiles.add(TileFactory.getLetterTile(letter));
        }

        return new Bag(tiles);
    }

    public int size() {
        return tiles.size();
    }

    public boolean isEmpty() {
        return tiles.isEmpty();
    }

    public DrawResult drawTiles(int numberOfTiles) {
        if (numberOfTiles > tiles.size()) {
            throw new IllegalArgumentException(
                    "Tried to draw " + numberOfTiles + " tiles. The bag only contains " + tiles.size() + ".");
        }

        List<Tile> remaining = new ArrayList<>(tiles);
        List<Tile> drawn = new ArrayList<>();

        for (int i = 0; i < numberOfTiles; i++) {
            int index = ThreadLocalRandom.current().nextInt(remaining.size());
            drawn.add(remaining.remove(index));
        }

        return new DrawResult(new Bag(remaining), drawn);
    }
}