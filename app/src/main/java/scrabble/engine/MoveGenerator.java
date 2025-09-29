package scrabble.engine;

import scrabble.core.Move;
import scrabble.core.PlayerView;
import scrabble.rules.DictionaryProvider;

import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import java.util.Iterator;

public class MoveGenerator {
    public static Stream<Move> streamLegalMoves(PlayerView playerView) {
        Iterator<Move> iterator = new LegalMoveIterator(playerView, DictionaryProvider.get());
        return StreamSupport.stream(
                Spliterators.spliteratorUnknownSize(iterator, Spliterator.ORDERED),
                false);
    }
}
