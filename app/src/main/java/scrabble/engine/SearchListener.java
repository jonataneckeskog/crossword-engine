package scrabble.engine;

import scrabble.core.Move;

import java.util.Map;

public interface SearchListener {
    void update(Map<Move, Double> winPercentagesMap);
}
