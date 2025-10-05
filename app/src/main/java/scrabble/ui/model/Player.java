package scrabble.ui.model;

import scrabble.core.Move;

public interface Player {
    int getId();

    String getName();

    boolean isHuman();

    default Move chooseMove(Game game, int timeMillis) {
        return null; // Human players don't implement this
    };
}
