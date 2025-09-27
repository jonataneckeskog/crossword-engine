package scrabble.engine;

import scrabble.core.PlayerView;

public interface Evaluator {
    int evaluate(PlayerView playerView);
}
