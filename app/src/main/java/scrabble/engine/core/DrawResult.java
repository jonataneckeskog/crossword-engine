package scrabble.engine.core;

import java.util.List;

public final class DrawResult {
    private final Bag newBag;
    private final List<Tile> drawnTiles;

    DrawResult(Bag bag, List<Tile> drawnTiles) {
        this.newBag = bag;
        this.drawnTiles = List.copyOf(drawnTiles);
    }

    public Bag getNewBag() {
        return newBag;
    }

    public List<Tile> getDrawnTiles() {
        return drawnTiles;
    }
}
