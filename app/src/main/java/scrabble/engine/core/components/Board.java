package scrabble.engine.core.components;

import java.util.Arrays;

import scrabble.engine.core.Move;
import scrabble.engine.util.BoardConstants;

public final class Board {
    private final Tile[] board;
    private final byte[] tileBonuses = BoardConstants.SCRABBLE_BOARD;

    private Board(Tile[] board) {
        this.board = board;
    }

    public static Board emptyBoard() {
        Tile[] tiles = new Tile[BoardConstants.TOTAL_SIZE];
        return new Board(tiles); // All tiles automatically get assigned to null
    }

    public static Board fromString(String boardString) {
        int totalSize = BoardConstants.TOTAL_SIZE;

        if (boardString.length() != totalSize)
            throw new IllegalArgumentException(
                    "Input string should be " + totalSize + " characters long. Is " + boardString.length());

        Tile[] tiles = new Tile[totalSize];
        for (int i = 0; i < totalSize; i++) {
            char letter = boardString.charAt(i);

            if (letter == '-') { // '-' is wildcard for empty square
                tiles[i] = null;
                continue;
            }

            if (!TileFactory.isValidLetter(letter)) {
                throw new IllegalArgumentException("Symbol " + letter + " is not a valid letter.");
            }

            tiles[i] = TileFactory.getTile(letter);
        }

        return new Board(tiles);
    }

    private static boolean isEmpty(Tile[] board, Position position) {
        return position == null || board[position.toIndex()] == null;
    }

    public Tile tileAt(int index) {
        return board[index];
    }

    public Tile tileAt(Position position) {
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
        Tile[] newBoard = board.clone();
        int totalScore = 0;

        // fast access to arrays
        Position[] positions = move.getPositions();
        Tile[] tiles = move.getTiles();

        // Place tiles
        for (int i = 0, n = positions.length; i < n; i++) {
            Position pos = positions[i];
            if (!Board.isEmpty(newBoard, pos)) {
                throw new IllegalArgumentException(
                        "Tried to place tile '" + tiles[i].letter() + "' at an invalid square.");
            }
            newBoard[pos.toIndex()] = tiles[i];
        }

        // Score main word
        Position startPosition = findWordStartingPosition(newBoard, positions[0], reverseStep);
        totalScore += scoreWord(newBoard, move, startPosition, step);

        if (move.tilesPlaced() == BoardConstants.RACK_SIZE) {
            totalScore += BoardConstants.BINGO_BONUS; // Bingo!!!
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

    private int scoreWord(Tile[] board, Move move, Position startPosition, Position.Step step) {
        int score = 0;
        int multiplier = 1;

        Position current = startPosition;
        while (!Board.isEmpty(board, current)) {
            int index = current.toIndex();
            Tile tile = board[index];
            int tileScore = tile.points();

            // Only newly placed tiles get letter/word bonuses
            if (move.isPlaced(current)) {
                byte bonus = tileBonuses[index];
                switch (bonus) {
                    case BoardConstants.NORMAL -> {
                    }
                    case BoardConstants.DOUBLE_LETTER -> tileScore *= 2;
                    case BoardConstants.TRIPLE_LETTER -> tileScore *= 3;
                    case BoardConstants.DOUBLE_WORD -> multiplier *= 2;
                    case BoardConstants.TRIPLE_WORD -> multiplier *= 3;
                    default -> throw new IllegalArgumentException(
                            "Unknown bonus value '" + bonus + "' at index " + index + ".");
                }
            }

            score += tileScore;
            current = current.tryStep(step);
        }

        return score * multiplier;
    }

    private Position findWordStartingPosition(Tile[] board, Position placedPosition, Position.Step step) {
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
        sb.append("Board (");
        sb.append(BoardConstants.SIZE);
        sb.append("x");
        sb.append(BoardConstants.SIZE);
        sb.append(")");
        for (int i = 0; i < board.length; i++) {
            if (i % BoardConstants.SIZE == 0) {
                sb.append(board[i].letter());
                sb.append("\n");
            } else if (i < board.length - 1) {
                sb.append(" ");
            }
            sb.append(board[i].letter());
        }
        return sb.toString();
    }
}