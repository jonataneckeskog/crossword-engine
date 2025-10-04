package scrabble.rules;

import java.util.List;
import java.util.Arrays;

import scrabble.core.Move;
import scrabble.core.components.Board;
import scrabble.rules.game.*;
import scrabble.core.Position;

public final class MoveValidator {
    private TrieDictionary dictionary;

    public MoveValidator(TrieDictionary dictionary) {
        this.dictionary = dictionary;
    }

    public boolean isValid(Board board, Move move) {
        // 1. Validate the move
        List<Position> positions = Arrays.asList(move.getPositions());
        if (positions.isEmpty()) {
            return false;
        }

        boolean sameRow = true;
        boolean sameCol = true;

        int baseRow = positions.get(0).row();
        int baseCol = positions.get(0).column();

        for (Position position : positions) {
            if (position.row() != baseRow) {
                sameRow = false;
            }
            if (position.column() != baseCol) {
                sameCol = false;
            }
        }

        if (!(sameRow || sameCol))
            return false;

        // 2. Make sure the board doesn't contain any letter where the move intends to
        // place
        for (Position position : positions) {
            if (!board.isEmpty(position))
                return false;
        }

        // 3. Place the move and validate the board
        Board newBoard = board.placeWord(move);

        char[][] rows = new char[BoardConstants.SIZE][BoardConstants.SIZE];
        char[][] columns = new char[BoardConstants.SIZE][BoardConstants.SIZE];

        for (int row = 0; row < BoardConstants.SIZE; row++) {
            for (int column = 0; column < BoardConstants.SIZE; column++) {
                char tile = newBoard.tileAt(row * BoardConstants.SIZE + column);
                rows[row][column] = tile;
                columns[column][row] = tile;
            }
        }

        for (char[] line : rows) {
            if (!isLineValid(line, dictionary))
                return false;
        }
        for (char[] line : columns) {
            if (!isLineValid(line, dictionary))
                return false;
        }

        return true;
    }

    private static boolean isLineValid(char[] line, TrieDictionary dictionary) {
        for (int i = 0; i < line.length; i++) {
            if (line[i] == GameConstants.EMPTY_SQUARE)
                continue;

            StringBuilder sb = new StringBuilder();
            for (int j = i; j < line.length; j++) {
                if (line[j] == GameConstants.EMPTY_SQUARE)
                    break;

                sb.append(line[j]);
                line[j] = GameConstants.EMPTY_SQUARE;
            }

            String word = sb.toString();

            if (word.length() > 1 && !dictionary.isWord(sb.toString()))
                return false;
        }

        return true;
    }
}
