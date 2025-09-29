package scrabble.engine;

import scrabble.core.components.Rack;
import scrabble.core.PlayerView;

import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class RackSimulator {
    public static Stream<Rack> stream(PlayerView playerView) {
        Iterator<Rack> iterator = new RackIterator(playerView);
        return StreamSupport.stream(
                Spliterators.spliteratorUnknownSize(iterator, Spliterator.ORDERED),
                false);
    }
}
