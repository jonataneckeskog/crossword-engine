package scrabble.engine;

import scrabble.core.Move;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SearchListener {
    // Thread-safe map to track move evaluations
    private final Map<Move, Double> moves = new ConcurrentHashMap<>();

    // Called by the search
    public void update(Map<Move, Double> winPercentagesMap) {
        moves.putAll(winPercentagesMap);
    }

    // Get all moves currently under analysis
    public Map<Move, Double> getMoves() {
        return Map.copyOf(moves);
    }

    // Get the current best move
    public Move getBestMove() {
        return moves.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
    }
}