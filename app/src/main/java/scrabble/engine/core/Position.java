package scrabble.engine.core;

import java.util.function.UnaryOperator;

import scrabble.engine.util.BoardConstants;

public record Position(int row, int column) {
    private static final int SIZE = BoardConstants.SIZE;

    public Position {
        if (!isInBounds()) {
            throw new IllegalArgumentException(
                    "Position " + row + "/" + column + " (row/column) is out of bounds for length " + SIZE);
        }
    }

    public Position step(Step step) {
        return step.apply(this);
    }

    public int toIndex() {
        return row * SIZE + column;
    }

    public Position down() {
        return new Position(row + 1, column);
    }

    public Position right() {
        return new Position(row, column + 1);
    }

    public Position up() {
        return new Position(row - 1, column);
    }

    public Position left() {
        return new Position(row, column - 1);
    }

    public boolean isInBounds() {
        return !(this.row < 0 || this.row >= SIZE || this.column < 0 || this.column >= SIZE);
    }

    public enum Step implements UnaryOperator<Position> {
        RIGHT(Position::right),
        LEFT(Position::left),
        DOWN(Position::down),
        UP(Position::up);

        private final UnaryOperator<Position> operator;

        Step(UnaryOperator<Position> operator) {
            this.operator = operator;
        }

        @Override
        public Position apply(Position position) {
            return operator.apply(position);
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