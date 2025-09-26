package scrabble.engine.core.components;

import scrabble.engine.rules.game.BoardConstants;

public record Position(int row, int column) {
    private static final int SIZE = BoardConstants.SIZE;

    public Position {
        if (row < 0 || row >= SIZE || column < 0 || column >= SIZE) {
            throw new IllegalArgumentException(
                    "Position " + row + "/" + column + " (row/column) is out of bounds for board size " + SIZE);
        }
    }

    // Safe step, returns null if outside the board
    public Position tryStep(Step step) {
        int newRow = row + step.deltaRow();
        int newCol = column + step.deltaCol();

        if (newRow < 0 || newRow >= SIZE || newCol < 0 || newCol >= SIZE) {
            return null;
        }
        return new Position(newRow, newCol);
    }

    public int toIndex() {
        return row * SIZE + column;
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

        public int deltaCol() {
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