package scrabble.engine;

import scrabble.core.GameState;
import scrabble.core.PlayerView;

import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class GameStateSimulator {
    public static Stream<GameState> stream(PlayerView playerView) {
        Iterator<GameState> iterator = new GameStateIterator(playerView);
        return StreamSupport.stream(
                Spliterators.spliteratorUnknownSize(iterator, Spliterator.ORDERED),
                false);
    }
}
