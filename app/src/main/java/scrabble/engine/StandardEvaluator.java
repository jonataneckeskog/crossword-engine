package scrabble.engine;

import java.util.Map;

import scrabble.core.GameState;

public class StandardEvaluator implements Evaluator {
    private static final Map<Character, Integer> TILE_UTILITIES = Map.ofEntries(
            Map.entry('A', 5), Map.entry('B', 3), Map.entry('C', 3), Map.entry('D', 4),
            Map.entry('E', 6), Map.entry('F', 2), Map.entry('G', 3), Map.entry('H', 3),
            Map.entry('I', 5), Map.entry('J', 1), Map.entry('K', 2), Map.entry('L', 5),
            Map.entry('M', 4), Map.entry('N', 5), Map.entry('O', 5), Map.entry('P', 4),
            Map.entry('Q', 0), Map.entry('R', 5), Map.entry('S', 7), Map.entry('T', 5),
            Map.entry('U', 5), Map.entry('V', 2), Map.entry('W', 2), Map.entry('X', 1),
            Map.entry('Y', 2), Map.entry('Z', 0), Map.entry('?', 10) // Blank tile
    );

    @Override
    public double evaluate(GameState gameState, int playerId) {
        double score = 0;

        // Score based on current score
        score += gameState.getScores()[playerId] - gameState.getScores()[1 - playerId];

        // Score based on tiles in rack
        char[] myRack = gameState.getRacks()[playerId].getLetters();
        char[] opponentsRack = gameState.getRacks()[1 - playerId].getLetters();
        for (int i = 0; i < myRack.length; i++) {
            score += TILE_UTILITIES.getOrDefault(myRack[i], 0) - TILE_UTILITIES.getOrDefault(opponentsRack[i], 0);
        }

        return score;
    }
}
