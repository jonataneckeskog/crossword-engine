package scrabble.engine.core;

import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public final class Bag {
    private final List<Tile> tiles;

    public Bag(List<Tile> tiles) {
        this.tiles = new ArrayList<>(tiles);
    }

    public static Bag standardBag() {
        // Standard Scrabble tile counts
        Map<Character, Integer> tileCounts = Map.ofEntries(
                Map.entry('A', 9), Map.entry('B', 2), Map.entry('C', 2),
                Map.entry('D', 4), Map.entry('E', 12), Map.entry('F', 2),
                Map.entry('G', 3), Map.entry('H', 2), Map.entry('I', 9),
                Map.entry('J', 1), Map.entry('K', 1), Map.entry('L', 4),
                Map.entry('M', 2), Map.entry('N', 6), Map.entry('O', 8),
                Map.entry('P', 2), Map.entry('Q', 1), Map.entry('R', 6),
                Map.entry('S', 4), Map.entry('T', 6), Map.entry('U', 4),
                Map.entry('V', 2), Map.entry('W', 2), Map.entry('X', 1),
                Map.entry('Y', 2), Map.entry('Z', 1), Map.entry('_', 2));

        List<Tile> tiles = new ArrayList<>();

        for (var entry : tileCounts.entrySet()) {
            char letter = entry.getKey();
            int count = entry.getValue();

            for (int i = 0; i < count; i++) {
                tiles.add(Tile.fromChar(letter));
            }
        }

        return new Bag(tiles);
    }

    public static Bag createFromString(String letters) {
        int length = letters.length();
        if (length > 100) {
            throw new IllegalArgumentException(
                    "String cannot contain more than 100 letters. It currently contains " + length + ".");
        }

        List<Tile> tiles = new ArrayList<>();

        for (int i = 0; i < length; i++) {
            char letter = letters.charAt(i);
            if (!Tile.isValidLetter(letter)) {
                throw new IllegalArgumentException("String contains invalid character '" + letter + "'");
            }

            tiles.add(Tile.fromChar(letter));
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