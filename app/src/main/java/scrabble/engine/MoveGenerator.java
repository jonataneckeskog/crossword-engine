package scrabble.engine;

import scrabble.core.Move;
import scrabble.core.PlayerView;
import scrabble.rules.AdvancedDictionary;

import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import java.util.Iterator;

public class MoveGenerator {
    public static Stream<Move> streamLegalMoves(PlayerView playerView, AdvancedDictionary dictionary) {
        Iterator<Move> iterator = new LegalMoveIterator(playerView, dictionary);
        return StreamSupport.stream(
                Spliterators.spliteratorUnknownSize(iterator, Spliterator.ORDERED),
                false);
    }
}
