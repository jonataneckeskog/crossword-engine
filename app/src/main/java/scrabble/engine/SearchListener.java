package scrabble.engine;

import scrabble.core.Move;

import java.util.Map;

public class SearchListener {
    private Map<Move, Double> moves;

    void update(Map<Move, Double> winPercentagesMap) {
        moves = winPercentagesMap;
    }

    public Map<Move, Double> getMoves() {
        return moves;
    }
}
