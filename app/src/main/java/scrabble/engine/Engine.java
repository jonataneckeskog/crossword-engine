package scrabble.engine;

import scrabble.core.Move;
import scrabble.core.PlayerView;

public interface Engine {
    Move chooseMove(PlayerView playerView, long timeMillis);

    void search(PlayerView playerView, SearchListener listener);

    // Stop the search
    void stop();
}
