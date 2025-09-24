package scrabble.engine.core.components;

import org.junit.jupiter.api.Test;
import scrabble.engine.core.Move;
import scrabble.engine.util.BoardConstants;

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

        board.placeWord(new Move(
                new Position[] { position },
                new Tile[] { tile }));
    }

    @Test
    void testScore() {
        Board board = Board.emptyBoard();
        Position[] positions1 = { new Position(2, 2), new Position(3, 2), new Position(4, 2) };
        Tile[] tiles1 = {
                TileFactory.getTile('l'),
                TileFactory.getTile('a'),
                TileFactory.getTile('t')
        };
        Board newBoard = board.placeWord(new Move(positions1, tiles1)).board();

        Position[] positions2 = { new Position(2, 3), new Position(3, 3) };
        Tile[] tiles2 = {
                TileFactory.getTile('l'),
                TileFactory.getTile('a')
        };
        PlacementResult placementResult1 = newBoard.placeWord(new Move(positions2, tiles2));
        int expectedScore1 = 2 + 2 * 2 + 2 * 2; // simplified example
        assertEquals(expectedScore1, placementResult1.score());

        Position[] positions3 = { new Position(4, 3), new Position(5, 3) };
        Tile[] tiles3 = {
                TileFactory.getTile('t'),
                TileFactory.getTile('e')
        };
        PlacementResult placementResult2 = placementResult1.board().placeWord(new Move(positions3, tiles3));
        int expectedScore2 = 6; // simplified example
        assertEquals(expectedScore2, placementResult2.score());
    }

    @Test
    void testPlaceSingleLetter() {
        Board board = Board.emptyBoard();
        Position pos = new Position(0, 2);
        Tile a = TileFactory.getTile('a');

        PlacementResult result = board.placeWord(new Move(
                new Position[] { pos },
                new Tile[] { a }));

        assertEquals(a, result.board().tileAt(pos));

        int expectedScore = 1;
        assertTrue(result.score() == expectedScore, "Single tile should score points");
    }

    @Test
    void testPlaceHorizontalWord() {
        Board board = Board.emptyBoard();
        Position[] positions = {
                new Position(2, 2), new Position(2, 3), new Position(2, 4)
        };
        Tile[] tiles = {
                TileFactory.getTile('c'),
                TileFactory.getTile('a'),
                TileFactory.getTile('t')
        };

        PlacementResult result = board.placeWord(new Move(positions, tiles));

        assertEquals(TileFactory.getTile('c'), result.board().tileAt(new Position(2, 2)));
        assertEquals(TileFactory.getTile('a'), result.board().tileAt(new Position(2, 3)));
        assertEquals(TileFactory.getTile('t'), result.board().tileAt(new Position(2, 4)));

        assertTrue(result.score() > 0, "Word should have a positive score");
    }

    @Test
    void testPlaceVerticalWord() {
        Board board = Board.emptyBoard();
        Position[] positions = {
                new Position(2, 2), new Position(3, 2), new Position(4, 2)
        };
        Tile[] tiles = {
                TileFactory.getTile('b'),
                TileFactory.getTile('a'),
                TileFactory.getTile('t')
        };

        PlacementResult result = board.placeWord(new Move(positions, tiles));

        assertEquals(TileFactory.getTile('a'), result.board().tileAt(new Position(3, 2)));
        assertTrue(result.score() > 0);
    }

    @Test
    void testHookedWord() {
        Board board = Board.emptyBoard();
        Position[] positions1 = {
                new Position(2, 2), new Position(2, 3), new Position(2, 4)
        };
        Tile[] tiles1 = {
                TileFactory.getTile('c'),
                TileFactory.getTile('a'),
                TileFactory.getTile('t')
        };
        PlacementResult result1 = board.placeWord(new Move(positions1, tiles1));

        Position[] positions2 = { new Position(1, 4) };
        Tile[] tiles2 = { TileFactory.getTile('a') };
        PlacementResult result2 = result1.board().placeWord(new Move(positions2, tiles2));

        assertEquals(TileFactory.getTile('a'), result2.board().tileAt(new Position(1, 4)));
        assertTrue(result2.score() > 0, "Hook word should add score");
    }

    @Test
    void testBingoBonus() {
        Board board = Board.emptyBoard();
        char[] word = "zephyrs".toCharArray(); // 7-letter word
        Position[] positions = new Position[word.length];
        Tile[] tiles = new Tile[word.length];
        for (int i = 0; i < word.length; i++) {
            positions[i] = new Position(2, 2 + i);
            tiles[i] = TileFactory.getTile(word[i]);
        }

        PlacementResult result = board.placeWord(new Move(positions, tiles));

        assertTrue(result.score() >= BoardConstants.BINGO_BONUS,
                "Bingo should include at least 50 bonus points");
    }

    @Test
    void testInvalidPlacementOnOccupiedSquare() {
        Board board = Board.emptyBoard();
        Position pos = new Position(2, 2);

        PlacementResult result = board.placeWord(new Move(
                new Position[] { pos },
                new Tile[] { TileFactory.getTile('a') }));

        // Try placing another tile on the same square
        assertThrows(IllegalArgumentException.class, () -> result.board().placeWord(new Move(
                new Position[] { pos },
                new Tile[] { TileFactory.getTile('b') })));
    }
}