package scrabble.engine;

import scrabble.core.GameState;

public class StandardEvaluator implements Evaluator {
    @Override
    public double evaluate(GameState gameState) {
        return gameState.getScores()[0];
    }
}
