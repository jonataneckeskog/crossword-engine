package scrabble.core.components;

import org.junit.jupiter.api.Test;
import scrabble.core.Move;
import scrabble.core.Position;
import scrabble.rules.game.*;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeAll;

class BoardTest {

    @BeforeAll
    static void setup() {
        GameRules.load("src/main/resources/scrabble.json"); // or correct path
    }

    @Test
    void testEmptyBoard() {
        Board board = Board.emptyBoard();
        for (int i = 0; i < BoardConstants.TOTAL_SIZE; i++) {
            assertEquals(GameConstants.EMPTY_SQUARE, board.tileAt(i));
        }
    }

    @Test
    void testFromStringValidation() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < BoardConstants.TOTAL_SIZE; i++)
            sb.append(GameConstants.EMPTY_SQUARE);
        assertDoesNotThrow(() -> Board.fromString(sb.toString()));

        sb.setCharAt(0, '1'); // invalid
        assertThrows(IllegalArgumentException.class, () -> Board.fromString(sb.toString()));
        assertThrows(IllegalArgumentException.class, () -> Board.fromString("ABC")); // too short
    }

    @Test
    void testTileAccessAndIsEmpty() {
        Board board = Board.emptyBoard();
        Position pos = new Position(0, 0);
        assertTrue(board.isEmpty(pos));
        assertEquals(GameConstants.EMPTY_SQUARE, board.tileAt(pos));
        assertEquals(GameConstants.EMPTY_SQUARE, board.tileAt(pos.toIndex()));
    }

    @Test
    void testPlaceWord() {
        Board board = Board.emptyBoard();
        Position[] positions = { new Position(0, 0), new Position(0, 1) };
        char[] tiles = { 'A', 'B' };
        Move move = new Move(positions, tiles);

        Board newBoard = board.placeWord(move);
        assertEquals('A', newBoard.tileAt(positions[0]));
        assertEquals('B', newBoard.tileAt(positions[1]));

        // Trying to place on the same square throws
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> newBoard.placeWord(move));
        assertTrue(ex.getMessage().contains("Tried to place tile"));
    }

    @Test
    void testEqualsHashCodeAndToString() {
        Board board1 = Board.emptyBoard();
        Board board2 = Board.emptyBoard();
        assertEquals(board1, board2);
        assertEquals(board1.hashCode(), board2.hashCode());
        assertTrue(board1.toString().contains(String.valueOf(BoardConstants.SIZE)));

        Board board3 = board1.placeWord(new Move(new Position[] { new Position(0, 0) }, new char[] { 'A' }));
        assertNotEquals(board1, board3);
    }

    @Test
    void testIsAnchor() {
        String boardString1 = "..............." +
                "..............." +
                "..............." +
                "..............." +
                "..............." +
                "..............." +
                "..............." +
                ".......A......." +
                "..............." +
                "..............." +
                "..............." +
                "..............." +
                "..............." +
                "..............." +
                "...............";
        boardString1 = boardString1.replace('.', GameConstants.EMPTY_SQUARE);
        Board board1 = Board.fromString(boardString1);

        assertTrue(board1.isAnchor(new Position(7, 6)));
        assertTrue(board1.isAnchor(new Position(7, 8)));
        assertTrue(board1.isAnchor(new Position(6, 7)));
        assertTrue(board1.isAnchor(new Position(8, 7)));
        assertFalse(board1.isAnchor(new Position(7, 7)));

        String boardString2 = "..............." +
                "..............." +
                "..............." +
                "..............." +
                "..............." +
                ".......A......." +
                ".......N......." +
                ".......D......." +
                "..............." +
                "..............." +
                "..............." +
                "..............." +
                "..............." +
                "..............." +
                "...............";
        boardString2 = boardString2.replace('.', GameConstants.EMPTY_SQUARE);
        Board board2 = Board.fromString(boardString2);

        assertTrue(board2.isAnchor(new Position(5, 6)));
        assertTrue(board2.isAnchor(new Position(6, 6)));
        assertTrue(board2.isAnchor(new Position(7, 6)));
        assertTrue(board2.isAnchor(new Position(4, 7)));
        assertTrue(board2.isAnchor(new Position(8, 7)));
        assertTrue(board2.isAnchor(new Position(5, 8)));
        assertTrue(board2.isAnchor(new Position(6, 8)));
        assertTrue(board2.isAnchor(new Position(7, 8)));
    }
}