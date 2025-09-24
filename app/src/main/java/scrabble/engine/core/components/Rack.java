package scrabble.engine.core.components;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

import scrabble.engine.util.BoardConstants;

public final class Rack {
    private final List<Tile> tiles;
    private static final int RACK_SIZE = BoardConstants.RACK_SIZE;

    private Rack(List<Tile> tiles) {
        this.tiles = tiles;
    }

    public static Rack emptyRack() {
        return new Rack(new ArrayList<>());
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

            tiles.add(TileFactory.getTile(letter));
        }

        return new Rack(tiles);
    }

    public DrawHandler drawFrom(Bag bag) {
        int numberOfTiles = bag.size() < RACK_SIZE - size() ? bag.size() : RACK_SIZE - size();
        DrawResult drawResult = bag.drawTiles(numberOfTiles);
        return new DrawHandler(drawResult.bag(), addTiles(drawResult.drawnTiles()));
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

            Tile tile = TileFactory.getTile(letter);

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

    public int size() {
        return tiles.size();
    }

    public boolean isEmpty() {
        return tiles.isEmpty();
    }

    // Returns an immutable version of tiles.
    // Since Tile is completely immutable, nothing can be changed.
    public List<Tile> getTiles() {
        return Collections.unmodifiableList(tiles);
    }

    public String letters() {
        StringBuilder sb = new StringBuilder(tiles.size());
        for (Tile tile : tiles) {
            sb.append(tile.letter());
        }
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Rack))
            return false;
        Rack other = (Rack) o;

        if (this.tiles.size() != other.tiles.size())
            return false;

        List<Tile> otherTilesCopy = new ArrayList<>(other.tiles);
        for (Tile tile : this.tiles) {
            if (!otherTilesCopy.remove(tile)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        for (Tile tile : tiles) {
            hash += tile.hashCode();
        }
        return hash;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Rack[");
        for (int i = 0; i < tiles.size(); i++) {
            sb.append(tiles.get(i).letter());
            if (i < tiles.size() - 1)
                sb.append(", ");
        }
        sb.append("]");
        return sb.toString();
    }
}
