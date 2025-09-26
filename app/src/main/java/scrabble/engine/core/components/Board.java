package scrabble.engine.core.components;

import java.util.Arrays;

import scrabble.engine.core.Move;
import scrabble.engine.rules.game.BagConstants;
import scrabble.engine.rules.game.BoardConstants;
import scrabble.engine.rules.game.GameConstants;
import scrabble.engine.rules.game.BagConstants.LetterData;

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

    private static boolean isEmpty(char[] board, Position position) {
        return position == null || board[position.toIndex()] == BoardConstants.EMPTY_SQUARE;
    }

    public char tileAt(int index) {
        return board[index];
    }

    public char tileAt(Position position) {
        return board[position.toIndex()];
    }

    public PlacementResult placeWord(Move move) {
        if (move.tilesPlaced() == 0) {
            throw new IllegalArgumentException("To place a word, there must exist tiles to be placed.");
        }

        final Position.Step step = move.getStep();
        final Position.Step reverseStep = Position.Step.reverseStep(step);
        final Position.Step otherStep = Position.Step.otherStep(step);
        final Position.Step reverseOtherStep = Position.Step.reverseStep(otherStep);

        // clone board for mutation
        char[] newBoard = board.clone();
        int totalScore = 0;

        // fast access to arrays
        Position[] positions = move.getPositions();
        char[] tiles = move.getTiles();

        // Place tiles
        for (int i = 0, n = positions.length; i < n; i++) {
            Position pos = positions[i];
            if (!Board.isEmpty(newBoard, pos)) {
                throw new IllegalArgumentException(
                        "Tried to place tile '" + tiles[i] + "' at an invalid square.");
            }
            newBoard[pos.toIndex()] = tiles[i];
        }

        // Score main word
        Position startPosition = findWordStartingPosition(newBoard, positions[0], reverseStep);
        totalScore += scoreWord(newBoard, move, startPosition, step);

        if (move.tilesPlaced() == GameConstants.RACK_SIZE) {
            totalScore += GameConstants.BINGO_BONUS; // Bingo!!!
        }

        // Score hooked words for each newly placed tile
        for (int i = 0, n = positions.length; i < n; i++) {
            Position pos = positions[i];
            Position pos1 = pos.tryStep(otherStep);
            Position pos2 = pos.tryStep(reverseOtherStep);

            // if either neighbor in the cross direction is occupied -> there's a cross word
            if (!Board.isEmpty(newBoard, pos1) || !Board.isEmpty(newBoard, pos2)) {
                Position hookStart = findWordStartingPosition(newBoard, pos, reverseOtherStep);
                totalScore += scoreWord(newBoard, move, hookStart, otherStep);
            }
        }

        return new PlacementResult(new Board(newBoard), totalScore);
    }

    private static int scoreWord(char[] board, Move move, Position startPosition, Position.Step step) {
        int totalScore = 0;
        int wordMultiplier = 1;

        Position current = startPosition;

        while (!Board.isEmpty(board, current)) {
            int index = current.toIndex();
            char tile = board[index];

            // Determine tile score
            int tileScore;
            if (BoardConstants.isBlank(tile)) {
                tileScore = 0;
            } else {
                LetterData data = BagConstants.TILE_DATA.get(tile);
                if (data == null) {
                    throw new IllegalArgumentException("Unknown tile '" + tile + "' at index " + index);
                }
                tileScore = data.getScore();
            }

            // Apply letter/word bonuses if this tile was placed this turn
            if (move.isPlaced(current)) {
                byte bonus = BoardConstants.SCRABBLE_BOARD[index];
                switch (bonus) {
                    case BoardConstants.NORMAL -> {
                    }
                    case BoardConstants.DOUBLE_LETTER -> tileScore *= 2;
                    case BoardConstants.TRIPLE_LETTER -> tileScore *= 3;
                    case BoardConstants.DOUBLE_WORD -> wordMultiplier *= 2;
                    case BoardConstants.TRIPLE_WORD -> wordMultiplier *= 3;
                    default -> throw new IllegalArgumentException(
                            "Unknown bonus value '" + bonus + "' at index " + index);
                }
            }

            totalScore += tileScore;
            current = current.tryStep(step);
        }

        return totalScore * wordMultiplier;
    }

    private static Position findWordStartingPosition(char[] board, Position placedPosition, Position.Step step) {
        Position previousPosition = placedPosition;
        if (Board.isEmpty(board, previousPosition)) {
            throw new IllegalArgumentException("Starting position is empty.");
        }

        Position currentPosition = previousPosition.tryStep(step);
        while (!Board.isEmpty(board, currentPosition)) {
            previousPosition = currentPosition;
            currentPosition = currentPosition.tryStep(step);
        }
        return previousPosition;
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
                int idx = r * BoardConstants.SIZE + c;
                char ch = board[idx];
                if (ch == BoardConstants.EMPTY_SQUARE) {
                    sb.append(BoardConstants.EMPTY_SQUARE); // or any nice placeholder
                } else {
                    sb.append(ch);
                }
                if (c < BoardConstants.SIZE - 1)
                    sb.append(' ');
            }
            sb.append('\n');
        }
        return sb.toString();
    }
}