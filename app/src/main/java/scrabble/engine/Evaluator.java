package scrabble.engine;

import scrabble.core.GameState;

public interface Evaluator {
    double evaluate(GameState gameState);
}
