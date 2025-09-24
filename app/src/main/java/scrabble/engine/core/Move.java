package scrabble.engine.core;

import java.util.function.BiConsumer;
import scrabble.engine.core.components.Position;
import scrabble.engine.core.components.Tile;
import scrabble.engine.core.components.Position.Step;
import scrabble.engine.util.game.BoardConstants;

public class Move {
    private final Position[] positions;
    private final Tile[] tiles;
    private final Step step;
    private final boolean[] placedLookup;

    public Move(Position[] positions, Tile[] tiles) {
        if (positions.length != tiles.length) {
            throw new IllegalArgumentException("Positions and tiles must have the same length.");
        }
        this.positions = positions;
        this.tiles = tiles;
        this.step = stepFromArray();

        placedLookup = new boolean[BoardConstants.TOTAL_SIZE];
        for (Position pos : positions) {
            placedLookup[pos.toIndex()] = true;
        }
    }

    public Position getStartPosition() {
        return positions[0];
    }

    private Step stepFromArray() {
        if (positions.length == 1) {
            return Step.RIGHT;
        }
        return positions[0].row() == positions[1].row() ? Step.RIGHT : Step.DOWN;
    }

    public void forAllMoves(BiConsumer<Position, Tile> action) {
        for (int i = 0; i < positions.length; i++) {
            action.accept(positions[i], tiles[i]);
        }
    }

    public int tilesPlaced() {
        return positions.length;
    }

    public Position[] getPositions() {
        return positions;
    }

    public Tile[] getTiles() {
        return tiles;
    }

    public Step getStep() {
        return step;
    }

    public boolean isPlaced(Position pos) {
        return placedLookup[pos.toIndex()];
    }
}