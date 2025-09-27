package scrabble.engine;

import scrabble.core.Move;

import java.util.Map;

public interface SearchListener {
    void onUpdate(Map<Move, Double> winPercentagesMap);
}
