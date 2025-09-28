package scrabble.core;

import scrabble.rules.game.BoardConstants;
import scrabble.core.components.Board;

public record Position(int row, int column) {
    public static Position fromIndex(int index) {
        return new Position(index / BoardConstants.SIZE, index % BoardConstants.SIZE);
    }

    public Position step(Step step) {
        return new Position(row + step.deltaRow(), column + step.deltaColumn());
    }

    // Safe step, returns null if outside the board
    public Position tryStep(Step step, Board board) {
        Position newPosition = step(step);
        if (board.isOutOfBounds(newPosition)) {
            return null;
        }
        return newPosition;
    }

    public int toIndex() {
        return row * BoardConstants.SIZE + column;
    }

    public enum Step {
        RIGHT(0, 1),
        LEFT(0, -1),
        DOWN(1, 0),
        UP(-1, 0);

        private final int dRow;
        private final int dCol;

        Step(int dRow, int dCol) {
            this.dRow = dRow;
            this.dCol = dCol;
        }

        public int deltaRow() {
            return dRow;
        }

        public int deltaColumn() {
            return dCol;
        }

        public static Step reverseStep(Step step) {
            return switch (step) {
                case DOWN -> UP;
                case UP -> DOWN;
                case LEFT -> RIGHT;
                case RIGHT -> LEFT;
            };
        }

        public static Step otherStep(Step step) {
            return switch (step) {
                case DOWN -> RIGHT;
                case UP -> LEFT;
                case LEFT -> UP;
                case RIGHT -> DOWN;
            };
        }
    }
}