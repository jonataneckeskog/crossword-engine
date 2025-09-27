package scrabble.engine;

import scrabble.core.GameState;

public interface Evaluator {
    int evaluate(GameState gameState);
}
