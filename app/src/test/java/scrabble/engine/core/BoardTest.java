package scrabble.engine.core;

import org.junit.jupiter.api.Test;

import scrabble.engine.util.BoardConstants;

import java.util.Map;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

public class BoardTest {
    private static StringBuilder sb = new StringBuilder();
    static {
        for (int i = 0; i < BoardConstants.TOTAL_SIZE; i++) {
            sb.append('-');
        }
    }
    private static final String BOARD_STRING_1 = sb.toString();

    private static final String BOARD_STRING_2;
    static {
        sb.setCharAt(2, 'a');
        BOARD_STRING_2 = sb.toString();
    }

    @Test
    void testFromString() {
        Board board1 = Board.fromString(BOARD_STRING_1);
        Board board2 = Board.fromString(BOARD_STRING_2);

        assertEquals(Board.emptyBoard(), board1);
        assertEquals(board2.tileAt(0), TileFactory.getTile('-')); // null.equals(null)
        assertEquals(board2.tileAt(2), TileFactory.getTile('a'));
    }

    @Test
    void testPlaceWord() {
        Board board = Board.emptyBoard();
        Position position = new Position(0, 0);
        Tile tile = TileFactory.getTile('a');

        Map<Position, Tile> move = new HashMap<>();
        move.put(position, tile);

        board.placeWord(move, Position.Step.DOWN);
    }

    @Test
    void testScore() {
        Board board = Board.emptyBoard();
        Map<Position, Tile> move1 = new HashMap<>();
        move1.put(new Position(2, 2), TileFactory.getTile('l'));
        move1.put(new Position(3, 2), TileFactory.getTile('a'));
        move1.put(new Position(4, 2), TileFactory.getTile('t'));

        Board newBoard = board.placeWord(move1, Position.Step.DOWN).board();

        Map<Position, Tile> move2 = new HashMap<>();
        move2.put(new Position(2, 3), TileFactory.getTile('l'));
        move2.put(new Position(3, 3), TileFactory.getTile('a'));

        PlacementResult placementResult = newBoard.placeWord(move2, Position.Step.DOWN);

        int expectedScore = 2 + 2 * 2 + 2 * 2; // IMPORTANT: Only correct for a normal scrabble board using english
                                               // tiles
        assertEquals(expectedScore, placementResult.score());
    }

    @Test
    void testPlaceSingleLetter() {
        Board board = Board.emptyBoard();
        Position pos = new Position(0, 2);
        Tile a = TileFactory.getTile('a');

        Map<Position, Tile> move = Map.of(pos, a);
        PlacementResult result = board.placeWord(move, Position.Step.RIGHT);

        assertEquals(a, result.board().tileAt(pos));

        int expectedScore = 1; // IMPORTANT: Only correct for a normal scrabble board using english tiles
        assertTrue(result.score() == expectedScore, "Single tile should score points");
    }

    @Test
    void testPlaceHorizontalWord() {
        Board board = Board.emptyBoard();
        Map<Position, Tile> move = new HashMap<>();
        move.put(new Position(2, 2), TileFactory.getTile('c'));
        move.put(new Position(2, 3), TileFactory.getTile('a'));
        move.put(new Position(2, 4), TileFactory.getTile('t'));

        PlacementResult result = board.placeWord(move, Position.Step.RIGHT);

        assertEquals(TileFactory.getTile('c'), result.board().tileAt(new Position(2, 2)));
        assertEquals(TileFactory.getTile('a'), result.board().tileAt(new Position(2, 3)));
        assertEquals(TileFactory.getTile('t'), result.board().tileAt(new Position(2, 4)));

        assertTrue(result.score() > 0, "Word should have a positive score");
    }

    @Test
    void testPlaceVerticalWord() {
        Board board = Board.emptyBoard();
        Map<Position, Tile> move = new HashMap<>();
        move.put(new Position(2, 2), TileFactory.getTile('b'));
        move.put(new Position(3, 2), TileFactory.getTile('a'));
        move.put(new Position(4, 2), TileFactory.getTile('t'));

        PlacementResult result = board.placeWord(move, Position.Step.DOWN);

        assertEquals(TileFactory.getTile('a'), result.board().tileAt(new Position(3, 2)));
        assertTrue(result.score() > 0);
    }

    @Test
    void testHookedWord() {
        Board board = Board.emptyBoard();
        Map<Position, Tile> move1 = Map.of(
                new Position(2, 2), TileFactory.getTile('c'),
                new Position(2, 3), TileFactory.getTile('a'),
                new Position(2, 4), TileFactory.getTile('t'));
        PlacementResult result1 = board.placeWord(move1, Position.Step.RIGHT);

        Map<Position, Tile> move2 = Map.of(new Position(1, 4), TileFactory.getTile('a'));
        PlacementResult result2 = result1.board().placeWord(move2, Position.Step.DOWN);

        assertEquals(TileFactory.getTile('a'), result2.board().tileAt(new Position(1, 4)));
        assertTrue(result2.score() > 0, "Hook word should add score");
    }

    @Test
    void testBingoBonus() {
        Board board = Board.emptyBoard();
        Map<Position, Tile> move = new HashMap<>();
        char[] word = "zephyrs".toCharArray(); // 7-letter word
        for (int i = 0; i < word.length; i++) {
            move.put(new Position(2, 2 + i), TileFactory.getTile(word[i]));
        }

        PlacementResult result = board.placeWord(move, Position.Step.RIGHT);

        assertTrue(result.score() >= BoardConstants.BINGO_BONUS,
                "Bingo should include at least 50 bonus points");
    }

    @Test
    void testInvalidPlacementOnOccupiedSquare() {
        Board board = Board.emptyBoard();
        Position pos = new Position(2, 2);
        Map<Position, Tile> move1 = Map.of(pos, TileFactory.getTile('a'));
        PlacementResult result = board.placeWord(move1, Position.Step.RIGHT);

        // Try placing another tile on the same square
        Map<Position, Tile> move2 = Map.of(pos, TileFactory.getTile('b'));
        assertThrows(IllegalArgumentException.class, () -> result.board().placeWord(move2, Position.Step.RIGHT));
    }

    @Test
    void testEmptyMoveThrows() {
        Board board = Board.emptyBoard();
        Map<Position, Tile> emptyMove = Map.of();

        assertThrows(IllegalArgumentException.class, () -> board.placeWord(emptyMove, Position.Step.RIGHT));
    }
}
