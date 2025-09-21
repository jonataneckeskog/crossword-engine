package scrabble.engine.core;

import java.util.Map;

public final class Board {
    private final Tile[] board;
    private final byte[] tileBonuses;

    private Board(Tile[] board) {
        this(board, SCRABBLE_BOARD);
    }

    private Board(Tile[] board, byte[] tileBonuses) {
        this.board = board;
        this.tileBonuses = tileBonuses;
    }

    public static final int SIZE = 15;
    private static final int TOTAL_SIZE = SIZE * SIZE;

    private static final byte NORMAL = 0;
    private static final byte DOUBLE_LETTER = 1;
    private static final byte TRIPLE_LETTER = 2;
    private static final byte DOUBLE_WORD = 3;
    private static final byte TRIPLE_WORD = 4;

    // Standard 15 x 15 Scrabble board
    private static final byte[] SCRABBLE_BOARD = {
            // Row 0
            TRIPLE_WORD, NORMAL, NORMAL, DOUBLE_LETTER, NORMAL, NORMAL, NORMAL, TRIPLE_WORD, NORMAL, NORMAL, NORMAL,
            DOUBLE_LETTER, NORMAL, NORMAL, TRIPLE_WORD,
            // Row 1
            NORMAL, DOUBLE_WORD, NORMAL, NORMAL, NORMAL, TRIPLE_LETTER, NORMAL, NORMAL, NORMAL, TRIPLE_LETTER, NORMAL,
            NORMAL, NORMAL, DOUBLE_WORD, NORMAL,
            // Row 2
            NORMAL, NORMAL, DOUBLE_WORD, NORMAL, NORMAL, NORMAL, DOUBLE_LETTER, NORMAL, DOUBLE_LETTER, NORMAL, NORMAL,
            NORMAL, DOUBLE_WORD, NORMAL, NORMAL,
            // Row 3
            DOUBLE_LETTER, NORMAL, NORMAL, DOUBLE_WORD, NORMAL, NORMAL, NORMAL, DOUBLE_LETTER, NORMAL, NORMAL, NORMAL,
            DOUBLE_WORD, NORMAL, NORMAL, DOUBLE_LETTER,
            // Row 4
            NORMAL, NORMAL, NORMAL, NORMAL, DOUBLE_WORD, NORMAL, NORMAL, NORMAL, NORMAL, NORMAL, DOUBLE_WORD, NORMAL,
            NORMAL, NORMAL, NORMAL,
            // Row 5
            NORMAL, TRIPLE_LETTER, NORMAL, NORMAL, NORMAL, TRIPLE_LETTER, NORMAL, NORMAL, NORMAL, TRIPLE_LETTER, NORMAL,
            NORMAL, NORMAL, TRIPLE_LETTER, NORMAL,
            // Row 6
            NORMAL, NORMAL, DOUBLE_LETTER, NORMAL, NORMAL, NORMAL, DOUBLE_LETTER, NORMAL, DOUBLE_LETTER, NORMAL, NORMAL,
            NORMAL, DOUBLE_LETTER, NORMAL, NORMAL,
            // Row 7
            TRIPLE_WORD, NORMAL, NORMAL, DOUBLE_LETTER, NORMAL, NORMAL, NORMAL, DOUBLE_WORD, NORMAL, NORMAL, NORMAL,
            DOUBLE_LETTER, NORMAL, NORMAL, TRIPLE_WORD,
            // Row 8
            NORMAL, NORMAL, DOUBLE_LETTER, NORMAL, NORMAL, NORMAL, DOUBLE_LETTER, NORMAL, DOUBLE_LETTER, NORMAL, NORMAL,
            NORMAL, DOUBLE_LETTER, NORMAL, NORMAL,
            // Row 9
            NORMAL, TRIPLE_LETTER, NORMAL, NORMAL, NORMAL, TRIPLE_LETTER, NORMAL, NORMAL, NORMAL, TRIPLE_LETTER, NORMAL,
            NORMAL, NORMAL, TRIPLE_LETTER, NORMAL,
            // Row 10
            NORMAL, NORMAL, NORMAL, NORMAL, DOUBLE_WORD, NORMAL, NORMAL, NORMAL, NORMAL, NORMAL, DOUBLE_WORD, NORMAL,
            NORMAL, NORMAL, NORMAL,
            // Row 11
            DOUBLE_LETTER, NORMAL, NORMAL, DOUBLE_WORD, NORMAL, NORMAL, NORMAL, DOUBLE_LETTER, NORMAL, NORMAL, NORMAL,
            DOUBLE_WORD, NORMAL, NORMAL, DOUBLE_LETTER,
            // Row 12
            NORMAL, NORMAL, DOUBLE_WORD, NORMAL, NORMAL, NORMAL, DOUBLE_LETTER, NORMAL, DOUBLE_LETTER, NORMAL, NORMAL,
            NORMAL, DOUBLE_WORD, NORMAL, NORMAL,
            // Row 13
            NORMAL, DOUBLE_WORD, NORMAL, NORMAL, NORMAL, TRIPLE_LETTER, NORMAL, NORMAL, NORMAL, TRIPLE_LETTER, NORMAL,
            NORMAL, NORMAL, DOUBLE_WORD, NORMAL,
            // Row 14
            TRIPLE_WORD, NORMAL, NORMAL, DOUBLE_LETTER, NORMAL, NORMAL, NORMAL, TRIPLE_WORD, NORMAL, NORMAL, NORMAL,
            DOUBLE_LETTER, NORMAL, NORMAL, TRIPLE_WORD
    };

    public static Board emptyBoard() {
        Tile[] tiles = new Tile[TOTAL_SIZE];
        for (int i = 0; i < TOTAL_SIZE; i++) {
            tiles[i] = null;
        }

        return new Board(tiles);
    }

    private static boolean isEmpty(Tile[] board, Position position) {
        return position == null || board[position.toIndex()] == null;
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
                        "Tried to place tile '" + entry.getValue().getLetter() + "' at an invalid square.");
            newBoard[entry.getKey().toIndex()] = entry.getValue();
        }
        Position startPosition = findWordStartingPosition(newBoard, letterPlacemensMap.keySet().iterator().next(),
                reverseStep);
        totalScore += scoreWord(newBoard, letterPlacemensMap, startPosition, step);
        if (letterPlacemensMap.size() == 7)
            totalScore += 50; // Bingo!!!

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
            tileScore += board[currentPosition.toIndex()].getPoints();

            if (letterPlacementsMap.containsKey(currentPosition)) {
                switch (tileBonuses[currentPosition.toIndex()]) {
                    case NORMAL -> {
                    }
                    case DOUBLE_LETTER -> tileScore *= 2;
                    case TRIPLE_LETTER -> tileScore *= 3;
                    case DOUBLE_WORD -> multiplier *= 2;
                    case TRIPLE_WORD -> multiplier *= 3;
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
            currentPosition = tryStep(previousPosition, step);
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
}