package scrabble.core.components;

import java.util.Arrays;

import scrabble.rules.game.BagConstants;
import scrabble.rules.game.GameConstants;

public final class Rack {
    private final byte[] frequencyMap;
    private final int size;

    private Rack() {
        frequencyMap = new byte[BagConstants.UNIQUE_TILES];
        size = 0;
    }

    private Rack(byte[] frequencyMap, int size) {
        this.frequencyMap = frequencyMap;
        this.size = size;
    }

    public static Rack emptyRack() {
        return new Rack();
    }

    public static Rack fromString(String letters) {
        int length = letters.length();
        if (length > GameConstants.RACK_SIZE) {
            throw new IllegalArgumentException(
                    "String cannot contain more than " + GameConstants.RACK_SIZE + " letters. It currently contains "
                            + length + ".");
        }

        byte[] frequencyMap = new byte[BagConstants.UNIQUE_TILES];
        int size = 0;

        for (int i = 0; i < length; i++) {
            char letter = letters.charAt(i);
            if (!BagConstants.isValidLetter(letter)) {
                throw new IllegalArgumentException("String contains invalid character '" + letter + "'");
            }

            frequencyMap[BagConstants.getIndex(letter)]++;
            size++;
        }

        return new Rack(frequencyMap, size);
    }

    public char[] getLetters() {
        char[] letters = new char[GameConstants.RACK_SIZE];
        int index = 0;
        for (int i = 0; i < BagConstants.UNIQUE_TILES; i++) {
            if (frequencyMap[i] != 0) {
                for (int j = 0; j < frequencyMap[i]; j++) {
                    letters[index] = BagConstants.INDEX_TO_CHAR[i];
                    index++;
                }
            }
        }
        return letters;
    }

    public DrawHandler drawFrom(Bag bag) {
        int numberOfTiles = bag.size() < GameConstants.RACK_SIZE - size ? bag.size()
                : GameConstants.RACK_SIZE - size;
        DrawResult drawResult = bag.drawTiles(numberOfTiles);
        return new DrawHandler(drawResult.bag(), addTiles(drawResult.drawnTiles()));
    }

    public Rack removeTiles(char[] tiles) {
        if (tiles.length == 0)
            return this;

        byte[] newFrequencyMap = frequencyMap.clone();
        int newSize = size;

        for (char tile : tiles) {
            if (!BagConstants.isValidLetter(tile)) {
                throw new IllegalArgumentException("Invalid tile: " + tile);
            }

            int index = BagConstants.getIndex(tile);
            if (newFrequencyMap[index] == 0)
                throw new IllegalStateException("Tile '" + tile + "' is not available in the bag");

            newFrequencyMap[index]--;
            newSize--;
        }

        return new Rack(newFrequencyMap, newSize);
    }

    public Rack addTiles(char[] tiles) {
        byte[] frequencyMap = this.frequencyMap.clone();
        int size = this.size;

        for (int i = 0, n = tiles.length; i < n; i++) {
            frequencyMap[BagConstants.getIndex(tiles[i])]++;
            size++;
        }

        if (size > GameConstants.RACK_SIZE)
            throw new IllegalArgumentException(
                    "Added too many tiles, rack maximum size is " + GameConstants.RACK_SIZE
                            + " but it would have contained "
                            + size
                            + " tiles.");

        return new Rack(frequencyMap, size);
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public byte[] getFrequencyMap() {
        return frequencyMap.clone();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Rack))
            return false;
        Rack other = (Rack) o;

        if (this.size != other.size)
            return false;

        return Arrays.equals(this.frequencyMap, other.frequencyMap);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(frequencyMap);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Rack[");

        boolean first = true;
        for (int i = 0; i < frequencyMap.length; i++) {
            for (int count = 0; count < frequencyMap[i]; count++) {
                if (!first)
                    sb.append(", ");
                sb.append(BagConstants.INDEX_TO_CHAR[i]);
                first = false;
            }
        }

        sb.append("]");
        return sb.toString();
    }
}
