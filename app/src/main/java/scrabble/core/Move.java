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

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Move))
            return false;
        Move other = (Move) o;

        if (positions.length != other.positions.length)
            return false;
        if (tiles.length != other.tiles.length)
            return false;

        for (int i = 0; i < positions.length; i++) {
            if (!positions[i].equals(other.positions[i])) {
                return false;
            }
            if (tiles[i] != other.tiles[i]) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        int result = 1;
        for (int i = 0; i < positions.length; i++) {
            result = 31 * result + positions[i].hashCode();
            result = 31 * result + Character.hashCode(tiles[i]);
        }
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append('\n');
        for (int i = 0; i < positions.length; i++) {
            sb.append(positions[i]);
            sb.append(" ");
            sb.append(tiles[i]);
            sb.append('\n');
        }
        return sb.toString();
    }
}