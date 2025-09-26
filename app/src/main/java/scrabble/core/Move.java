package scrabble.core;

import java.util.function.BiConsumer;

import scrabble.core.Position.Step;
import scrabble.rules.game.BoardConstants;

public class Move {
    private final Position[] positions;
    private final char[] tiles;
    private final Step step;
    private final boolean[] placedLookup;

    public Move(Position[] positions, char[] tiles) {
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

    public void forAllMoves(BiConsumer<Position, Character> action) {
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

    public char[] getTiles() {
        return tiles;
    }

    public Step getStep() {
        return step;
    }

    public boolean isPlaced(Position pos) {
        return placedLookup[pos.toIndex()];
    }
}