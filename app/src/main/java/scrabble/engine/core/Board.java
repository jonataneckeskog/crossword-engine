package scrabble.engine.core;

import java.util.Arrays;
import java.util.Map;
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

    public PlacementResult placeWord(Map<Position, Tile> letterPlacemensMap, Position.Step step) {
        if (letterPlacemensMap.size() == 0) {
            throw new IllegalArgumentException("To place a word, there must exist tiles to be placed.");
        }

        Position.Step reverseStep = Position.Step.reverseStep(step);
        Position.Step otherStep = Position.Step.otherStep(step);
        Position.Step reverseOtherStep = Position.Step.reverseStep(otherStep);

        Tile[] newBoard = board.clone();
        int totalScore = 0;

        // Place tiles and score first word
        for (Map.Entry<Position, Tile> entry : letterPlacemensMap.entrySet()) {
            if (!Board.isEmpty(newBoard, entry.getKey()))
                throw new IllegalArgumentException(
                        "Tried to place tile '" + entry.getValue().letter() + "' at an invalid square.");
            newBoard[entry.getKey().toIndex()] = entry.getValue();
        }
        Position startPosition = findWordStartingPosition(newBoard, letterPlacemensMap.keySet().iterator().next(),
                reverseStep);
        totalScore += scoreWord(newBoard, letterPlacemensMap, startPosition, step);
        if (letterPlacemensMap.size() == BoardConstants.RACK_SIZE)
            totalScore += BoardConstants.BINGO_BONUS; // Bingo!!!

        // Score hooked words on placed tiles
        for (Map.Entry<Position, Tile> entry : letterPlacemensMap.entrySet()) {
            Position position = entry.getKey();

            Position position1 = tryStep(position, otherStep);
            Position position2 = tryStep(position, reverseOtherStep);

            if (!Board.isEmpty(newBoard, position1)
                    || !Board.isEmpty(newBoard, position2)) {
                Position hookStartPosition = findWordStartingPosition(newBoard, position, reverseOtherStep);
                totalScore += scoreWord(newBoard, letterPlacemensMap, hookStartPosition, otherStep);
            }
        }

        return new PlacementResult(new Board(newBoard), totalScore);
    }

    private int scoreWord(Tile[] board, Map<Position, Tile> letterPlacementsMap, Position startPosition,
            Position.Step step) {
        int score = 0;
        int multiplier = 1;

        Position currentPosition = startPosition;
        while (!Board.isEmpty(board, currentPosition)) {
            int tileScore = 0;
            tileScore += board[currentPosition.toIndex()].points();

            if (letterPlacementsMap.containsKey(currentPosition)) {
                switch (tileBonuses[currentPosition.toIndex()]) {
                    case BoardConstants.NORMAL -> {
                    }
                    case BoardConstants.DOUBLE_LETTER -> tileScore *= 2;
                    case BoardConstants.TRIPLE_LETTER -> tileScore *= 3;
                    case BoardConstants.DOUBLE_WORD -> multiplier *= 2;
                    case BoardConstants.TRIPLE_WORD -> multiplier *= 3;
                    default -> throw new IllegalArgumentException(
                            "This tile contains the unknown bonus value '" + tileBonuses[currentPosition.toIndex()]
                                    + "'.");
                }
            }

            score += tileScore;

            currentPosition = tryStep(currentPosition, step);
        }

        return score * multiplier;
    }

    private Position findWordStartingPosition(Tile[] board, Position placedPosition, Position.Step step) {
        Position previousPosition = placedPosition;
        if (Board.isEmpty(board, previousPosition)) {
            throw new IllegalArgumentException("Starting position is empty.");
        }

        Position currentPosition = tryStep(previousPosition, step);
        while (!Board.isEmpty(board, currentPosition)) {
            previousPosition = currentPosition;
            currentPosition = tryStep(currentPosition, step);
        }

        return previousPosition;
    }

    private Position tryStep(Position position, Position.Step step) {
        try {
            return position.step(step);
        } catch (IllegalArgumentException e) {
            return null;
        }
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