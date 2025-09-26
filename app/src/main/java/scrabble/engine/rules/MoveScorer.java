package scrabble.engine.rules;

import scrabble.engine.core.Move;
import scrabble.engine.core.components.Board;

public interface MoveScorer {
    int score(Board board, Move move);
}
