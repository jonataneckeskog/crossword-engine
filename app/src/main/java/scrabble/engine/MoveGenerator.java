package scrabble.engine;

import scrabble.core.Move;
import scrabble.core.PlayerView;
import scrabble.rules.TrieDictionary;

import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import java.util.Iterator;

public class MoveGenerator {
    private TrieDictionary dictionary;

    public MoveGenerator(TrieDictionary dictionary) {
        this.dictionary = dictionary;
    }

    public Stream<Move> streamLegalMoves(PlayerView playerView) {
        Iterator<Move> iterator = new LegalMoveIterator(playerView, dictionary);
        return StreamSupport.stream(
                Spliterators.spliteratorUnknownSize(iterator, Spliterator.ORDERED),
                false);
    }
}
