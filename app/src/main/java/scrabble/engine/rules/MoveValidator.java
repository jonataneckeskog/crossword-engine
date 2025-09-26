package scrabble.engine.rules;

import scrabble.engine.core.Move;
import scrabble.engine.core.components.Board;

public interface MoveValidator {
    public boolean isValid(Board board, Move move);
}
