package scrabble.core.components;

import java.util.Map;

import java.util.Arrays;

import java.util.Random;

import scrabble.rules.game.BagConstants;
import scrabble.rules.game.BagConstants.LetterData;

public final class Bag {
    private final byte[] frequencyMap;
    private int size;

    private static final Random RANDOM = new Random();

    private Bag() {
        frequencyMap = new byte[BagConstants.UNIQUE_TILES];
        size = 0;
    }

    private Bag(byte[] frequencyMap, int size) {
        this.frequencyMap = frequencyMap;
        this.size = size;
    }

    public static Bag standardBag() {
        byte[] frequencyMap = new byte[BagConstants.UNIQUE_TILES];
        int size = 0;

        for (Map.Entry<Character, LetterData> entry : BagConstants.TILE_DATA.entrySet()) {
            int count = entry.getValue().getCount();
            frequencyMap[BagConstants.getIndex(entry.getKey())] = (byte) count;
            size += count;
        }

        return new Bag(frequencyMap, size);
    }

    public static Bag fromString(String letters) {
        int length = letters.length();
        if (length > BagConstants.TILE_COUNT) {
            throw new IllegalArgumentException(
                    "String cannot contain more than " + BagConstants.TILE_COUNT + " letters. It currently contains "
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

        return new Bag(frequencyMap, size);
    }

    public DrawResult drawTiles(int numberOfTiles) {
        if (numberOfTiles < 0)
            throw new IllegalArgumentException("Cannot draw negative number of tiles");
        if (numberOfTiles > size)
            throw new IllegalArgumentException("Not enough tiles left in the bag");

        char[] drawnTiles = new char[numberOfTiles];
        byte[] newFrequencyMap = frequencyMap.clone();
        int newSize = size;

        for (int n = 0; n < numberOfTiles; n++) {
            int r = RANDOM.nextInt(newSize);
            int cumulative = 0;

            for (int i = 0; i < newFrequencyMap.length; i++) {
                cumulative += newFrequencyMap[i];
                if (r < cumulative) {
                    drawnTiles[n] = BagConstants.INDEX_TO_CHAR[i];
                    newFrequencyMap[i]--;
                    newSize--;
                    break;
                }
            }
        }

        return new DrawResult(new Bag(newFrequencyMap, newSize), drawnTiles);
    }

    public Bag removeTiles(char[] tiles) {
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

        return new Bag(newFrequencyMap, newSize);
    }

    public Bag addTiles(char[] tiles) {
        byte[] frequencyMap = this.frequencyMap.clone();
        int size = this.size;

        for (int i = 0, n = tiles.length; i < n; i++) {
            frequencyMap[BagConstants.getIndex(tiles[i])]++;
            size++;
        }

        if (size > BagConstants.TILE_COUNT)
            throw new IllegalArgumentException(
                    "Added too many tiles, rack maximum size is " + BagConstants.TILE_COUNT
                            + " but it would have contained "
                            + size
                            + " tiles.");

        return new Bag(frequencyMap, size);
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
        if (!(o instanceof Bag))
            return false;
        Bag other = (Bag) o;

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
        sb.append("Bag[");

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