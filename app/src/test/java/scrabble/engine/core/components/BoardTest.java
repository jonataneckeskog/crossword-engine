package scrabble.engine.core.components;

import org.junit.jupiter.api.Test;
import scrabble.engine.core.Move;
import scrabble.engine.rules.game.BoardConstants;
import scrabble.engine.rules.game.GameConstants;

import static org.junit.jupiter.api.Assertions.*;

public class BoardTest {

    private static final String BOARD_STRING_EMPTY;
    private static final String BOARD_STRING_WITH_BLANK;

    static {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < BoardConstants.TOTAL_SIZE; i++)
            sb.append(BoardConstants.EMPTY_SQUARE);
        BOARD_STRING_EMPTY = sb.toString();

        sb.setCharAt(2, 'a'); // lowercase represents a blank tile
        BOARD_STRING_WITH_BLANK = sb.toString();
    }

    @Test
    void testFromString() {
        Board emptyBoard = Board.fromString(BOARD_STRING_EMPTY);
        Board boardWithBlank = Board.fromString(BOARD_STRING_WITH_BLANK);

        assertEquals(Board.emptyBoard(), emptyBoard);
        assertEquals(BoardConstants.EMPTY_SQUARE, emptyBoard.tileAt(0));
        assertEquals('a', boardWithBlank.tileAt(2)); // blank tile
    }

    @Test
    void testPlaceSingleLetter() {
        Board board = Board.emptyBoard();
        Position pos = new Position(0, 0);
        char tile = 'A';

        PlacementResult result = board.placeWord(new Move(
                new Position[] { pos },
                new char[] { tile })); // Assuming Move now uses char[]

        assertEquals(tile, result.board().tileAt(pos));
        assertTrue(result.score() > 0);
    }

    @Test
    void testPlaceHorizontalWord() {
        Board board = Board.emptyBoard();
        Position[] positions = { new Position(2, 2), new Position(2, 3), new Position(2, 4) };
        char[] tiles = { 'c', 'A', 't' };

        PlacementResult result = board.placeWord(new Move(positions, tiles));

        assertEquals('c', result.board().tileAt(new Position(2, 2)));
        assertEquals('A', result.board().tileAt(new Position(2, 3)));
        assertEquals('t', result.board().tileAt(new Position(2, 4)));

        assertTrue(result.score() > 0);
    }

    @Test
    void testPlaceVerticalWord() {
        Board board = Board.emptyBoard();
        Position[] positions = { new Position(2, 2), new Position(3, 2), new Position(4, 2) };
        char[] tiles = { 'b', 'A', 't' };

        PlacementResult result = board.placeWord(new Move(positions, tiles));

        assertEquals('b', result.board().tileAt(new Position(2, 2)));
        assertTrue(result.score() > 0);
    }

    @Test
    void testHookedWord() {
        Board board = Board.emptyBoard();
        Position[] positions1 = { new Position(2, 2), new Position(2, 3), new Position(2, 4) };
        char[] tiles1 = { 'c', 'A', 't' };
        PlacementResult result1 = board.placeWord(new Move(positions1, tiles1));

        Position[] positions2 = { new Position(1, 4) };
        char[] tiles2 = { 'A' };
        PlacementResult result2 = result1.board().placeWord(new Move(positions2, tiles2));

        assertEquals('A', result2.board().tileAt(new Position(1, 4)));
        assertTrue(result2.score() > 0);
    }

    @Test
    void testBingoBonus() {
        Board board = Board.emptyBoard();
        char[] word = "zephyrs".toCharArray(); // 7-letter word
        Position[] positions = new Position[word.length];
        char[] tiles = new char[word.length];
        for (int i = 0; i < word.length; i++) {
            positions[i] = new Position(2, 2 + i);
            tiles[i] = word[i];
        }

        PlacementResult result = board.placeWord(new Move(positions, tiles));

        assertTrue(result.score() >= GameConstants.BINGO_BONUS,
                "Bingo should include at least the bonus points");
    }

    @Test
    void testInvalidPlacementOnOccupiedSquare() {
        Board board = Board.emptyBoard();
        Position pos = new Position(2, 2);

        PlacementResult first = board.placeWord(new Move(
                new Position[] { pos },
                new char[] { 'a' }));

        // Placing another tile on the same square should fail
        assertThrows(IllegalArgumentException.class, () -> first.board().placeWord(new Move(
                new Position[] { pos },
                new char[] { 'b' })));
    }

    @Test
    void testBlankTileScoring() {
        Board board = Board.emptyBoard();
        Position pos = new Position(0, 0);
        char blank = 'a'; // lowercase = blank

        PlacementResult result = board.placeWord(new Move(
                new Position[] { pos },
                new char[] { blank }));

        assertEquals(blank, result.board().tileAt(pos));
        assertEquals(0, result.score(), "Blank tile should score 0 points");
    }
}