package scrabble.core.components;

import java.util.Arrays;

import scrabble.core.Move;
import scrabble.core.Position;
import scrabble.rules.game.BoardConstants;
import scrabble.rules.game.BagConstants;

public final class Board {
    private final char[] board;

    private Board(char[] board) {
        this.board = board;
    }

    public static Board emptyBoard() {
        char[] tiles = new char[BoardConstants.TOTAL_SIZE];
        Arrays.fill(tiles, BoardConstants.EMPTY_SQUARE);
        return new Board(tiles);
    }

    public static Board fromString(String boardString) {
        int totalSize = BoardConstants.TOTAL_SIZE;

        if (boardString.length() != totalSize)
            throw new IllegalArgumentException(
                    "Input string should be " + totalSize + " characters long. Is " + boardString.length());

        char[] tiles = new char[totalSize];
        for (int i = 0; i < totalSize; i++) {
            char letter = boardString.charAt(i);

            if (!(BagConstants.isValidLetter(Character.toUpperCase(letter))
                    || (letter == BoardConstants.EMPTY_SQUARE))) {
                throw new IllegalArgumentException("Symbol " + letter + " is not a valid letter.");
            }

            tiles[i] = letter;
        }

        return new Board(tiles);
    }

    public boolean isOutOfBounds(int index) {
        return (index < 0 || index >= BoardConstants.TOTAL_SIZE);
    }

    public boolean isOutOfBounds(Position position) {
        return (position.row() < 0 || position.row() >= BoardConstants.SIZE || position.column() < 0
                || position.column() >= BoardConstants.SIZE);
    }

    public boolean isEmpty(int index) {
        return board[index] == BoardConstants.EMPTY_SQUARE;
    }

    public boolean isEmpty(Position position) {
        return board[position.toIndex()] == BoardConstants.EMPTY_SQUARE;
    }

    public char tileAt(int index) {
        return board[index];
    }

    public char tileAt(Position position) {
        return board[position.toIndex()];
    }

    public Board placeWord(Move move) {
        if (move.tilesPlaced() == 0) {
            throw new IllegalArgumentException("To place a word, there must exist tiles to be placed.");
        }

        // clone board for mutation
        char[] newBoard = board.clone();

        // fast access to arrays
        Position[] positions = move.getPositions();
        char[] tiles = move.getTiles();

        // Place tiles
        for (int i = 0, n = positions.length; i < n; i++) {
            Position position = positions[i];
            if (!isEmpty(position)) {
                throw new IllegalArgumentException(
                        "Tried to place tile '" + tiles[i] + "' at an invalid square.");
            }
            newBoard[position.toIndex()] = tiles[i];
        }

        return new Board(newBoard);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Board))
            return false;
        Board other = (Board) o;
        return Arrays.equals(this.board, other.board);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(board);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Board (")
                .append(BoardConstants.SIZE)
                .append("x")
                .append(BoardConstants.SIZE)
                .append(")\n");

        for (int r = 0; r < BoardConstants.SIZE; r++) {
            for (int c = 0; c < BoardConstants.SIZE; c++) {
                int index = r * BoardConstants.SIZE + c;
                char letter = board[index];
                sb.append(letter == BoardConstants.EMPTY_SQUARE ? BoardConstants.EMPTY_SQUARE : letter);
                if (c < BoardConstants.SIZE - 1)
                    sb.append(' ');
            }
            sb.append('\n');
        }
        return sb.toString();
    }
}