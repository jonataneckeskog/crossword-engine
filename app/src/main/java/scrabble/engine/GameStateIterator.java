package scrabble.engine;

import scrabble.core.GameState;
import scrabble.core.PlayerView;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class GameStateIterator implements Iterator<GameState> {
    private PlayerView playerView;
    private GameState nextState = null;

    public GameStateIterator(PlayerView playerView) {
        this.playerView = playerView;
        advance();
    }

    private void advance() {
        // TODO: derive the next GameState from playerView
        // nextState = ...;
        // if no more states, set nextState = null;
    }

    @Override
    public boolean hasNext() {
        return nextState != null;
    }

    @Override
    public GameState next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        GameState current = nextState;
        advance();
        return current;
    }
}
