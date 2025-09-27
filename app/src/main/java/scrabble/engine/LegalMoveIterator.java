package scrabble.engine;

import scrabble.core.*;
import scrabble.core.Position.Step;
import scrabble.core.components.*;
import scrabble.rules.AdvancedDictionary;
import scrabble.rules.game.BoardConstants;

import java.util.Iterator;

public class LegalMoveIterator implements Iterator<Move> {
    private final AdvancedDictionary dictionary;
    private final Board board;
    private final char[] rack;

    private int square;
    private Position.Step tempStep = Position.Step.UP;
    private char[] tempRack;
    private Move nextMove = null;

    public LegalMoveIterator(PlayerView playerView, AdvancedDictionary dictionary) {
        this.dictionary = dictionary;
        board = playerView.getBoard();
        rack = playerView.getRack().getLetters();
        tempRack = rack.clone();
        advance();
    }

    // "Advanced"
    private void advance() {
        for (int square = this.square; square < BoardConstants.TOTAL_SIZE; square++) {
            // 1. If the square contains no anchor point -> go to next square
            if (board.isEmpty(square))
                continue;

            // 2. Square is full. Get the square (char).
            char anchor = board.tileAt(square);

            // 3. Explore in set direction for more adjacent tiles
            searchDirection(tempStep, anchor);
        }
    }

    private void searchDirection(Position.Step step, char anchor) {
        Position.Step reverseStep = Step.reverseStep(step);

    }

    @Override
    public boolean hasNext() {
        return nextMove != null;
    }

    @Override
    public Move next() {
        Move move = nextMove;
        advance();
        return move;
    }

}
