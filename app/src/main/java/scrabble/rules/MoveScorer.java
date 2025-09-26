package scrabble.rules;

import scrabble.core.components.Board;
import scrabble.core.Move;
import scrabble.core.Position;
import scrabble.rules.game.BagConstants;
import scrabble.rules.game.BoardConstants;
import scrabble.rules.game.GameConstants;
import scrabble.rules.game.BagConstants.LetterData;

public final class MoveScorer {

    // Private constructor to prevent instantiation
    private MoveScorer() {
    }

    public static int score(Board board, Move move) {
        if (move.tilesPlaced() == 0) {
            throw new IllegalArgumentException("Move must place at least one tile.");
        }

        int totalScore = 0;
        Position[] positions = move.getPositions();

        // Score main word
        Position.Step step = move.getStep();
        Position start = findWordStart(board, positions[0], Position.Step.reverseStep(step));
        totalScore += scoreWord(board, move, start, step);

        // Bonus for placing all tiles
        if (move.tilesPlaced() == GameConstants.RACK_SIZE) {
            totalScore += GameConstants.BINGO_BONUS;
        }

        // Score cross words
        Position.Step otherStep = Position.Step.otherStep(step);
        Position.Step reverseOtherStep = Position.Step.reverseStep(otherStep);

        for (Position pos : positions) {
            Position neighbor1 = pos.tryStep(otherStep);
            Position neighbor2 = pos.tryStep(reverseOtherStep);

            if ((neighbor1 != null && !board.isEmpty(neighbor1)) ||
                    (neighbor2 != null && !board.isEmpty(neighbor2))) {

                Position hookStart = findWordStart(board, pos, reverseOtherStep);
                totalScore += scoreWord(board, move, hookStart, otherStep);
            }
        }

        return totalScore;
    }

    private static int scoreWord(Board board, Move move, Position start, Position.Step step) {
        int wordMultiplier = 1;
        int wordScore = 0;
        Position pos = start;

        while (pos != null && !board.isEmpty(pos)) {
            char tile = board.tileAt(pos);
            int tileScore = tileScore(tile);

            if (move.isPlaced(pos)) {
                byte bonus = BoardConstants.SCRABBLE_BOARD[pos.toIndex()];
                switch (bonus) {
                    case BoardConstants.DOUBLE_LETTER -> tileScore *= 2;
                    case BoardConstants.TRIPLE_LETTER -> tileScore *= 3;
                    case BoardConstants.DOUBLE_WORD -> wordMultiplier *= 2;
                    case BoardConstants.TRIPLE_WORD -> wordMultiplier *= 3;
                    case BoardConstants.NORMAL -> {
                    } // no change
                    default -> throw new IllegalArgumentException("Unknown bonus: " + bonus);
                }
            }

            wordScore += tileScore;
            pos = pos.tryStep(step);
        }

        return wordScore * wordMultiplier;
    }

    private static int tileScore(char tile) {
        if (BoardConstants.isBlank(tile))
            return 0;
        LetterData data = BagConstants.TILE_DATA.get(tile);
        if (data == null)
            throw new IllegalArgumentException("Unknown tile: " + tile);
        return data.getScore();
    }

    private static Position findWordStart(Board board, Position pos, Position.Step step) {
        Position prev = pos;
        Position current = pos.tryStep(step);

        while (current != null && !board.isEmpty(current)) {
            prev = current;
            current = current.tryStep(step);
        }

        return prev;
    }
}