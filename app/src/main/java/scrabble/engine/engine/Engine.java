package scrabble.engine.engine;

import scrabble.engine.core.Move;
import scrabble.engine.core.PlayerView;

public interface Engine {
    Move chooseMove(PlayerView playerView);
}
