package scrabble.ui.controller;

import scrabble.rules.game.BagConstants;
import scrabble.ui.view.BoardTile;
import scrabble.ui.view.BonusType;
import scrabble.ui.view.LetterTile;

public class TileFactory {
    public static LetterTile getLetterTile(char letter) {
        return new LetterTile(BonusType.NONE.getColor(), letter, BagConstants.TILE_DATA.get(letter).getScore());
    }

    public static BoardTile getBoardTile(int bonus) {
        return new BoardTile(BonusType.fromValue(bonus));
    }
}
