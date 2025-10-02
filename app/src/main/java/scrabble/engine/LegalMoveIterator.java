package scrabble.engine;

import scrabble.core.*;
import scrabble.core.components.*;
import scrabble.rules.TrieDictionary;
import scrabble.rules.game.BagConstants;
import scrabble.rules.game.BoardConstants;
import scrabble.rules.game.GameConstants;
import scrabble.rules.TrieNode;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.NoSuchElementException;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public class LegalMoveIterator implements Iterator<Move> {
    // Fields
    private TrieDictionary dictionary;
    private final Board board;
    private final char[] rack;
    private final boolean isFirstMove;
    private final TrieNode trieRoot;

    // Iterator variable
    private Deque<Move> nextMoves = null;

    // Temporary fiels, used for iteration
    private int square;
    private int currentRow;
    private int currentCol;

    private final char[][] horiLines = new char[BoardConstants.SIZE][BoardConstants.SIZE];
    private final char[][] vertLines = new char[BoardConstants.SIZE][BoardConstants.SIZE];

    public void initializeBuffers() {
        for (int i = 0; i < BoardConstants.SIZE; i++) {
            for (int j = 0; j < BoardConstants.SIZE; j++) {
                char tile = board.tileAt(new Position(i, j));
                horiLines[i][j] = tile;
                vertLines[j][i] = tile;
            }
        }
    }

    public LegalMoveIterator(PlayerView playerView, TrieDictionary dictionary) {
        this.dictionary = dictionary;
        board = playerView.getBoard();
        rack = playerView.getRack().getLetters();
        isFirstMove = playerView.isFirstMove();
        square = 0;
        trieRoot = dictionary.getRoot();
        initializeBuffers();
        advance();
    }

    // "Advanced" starts now
    private void advance() {
        nextMoves = null;

        if (isFirstMove) {
            // Special case: first move must cover center square
            currentRow = BoardConstants.SIZE / 2;
            currentCol = BoardConstants.SIZE / 2;

            char[] horiBuffer = horiLines[currentRow].clone();
            boolean[] horiPlaced = new boolean[BoardConstants.SIZE];

            char[] vertBuffer = vertLines[currentCol].clone();
            boolean[] vertPlaced = new boolean[BoardConstants.SIZE];

            reverseBuild(rack.clone(), horiBuffer, horiPlaced, currentCol, rack.length, true);
            reverseBuild(rack.clone(), vertBuffer, vertPlaced, currentRow, rack.length, false);

            return;
        }

        for (; this.square < BoardConstants.TOTAL_SIZE; this.square++) {
            // 1. Create a position from the square and check if it is an anchor. If it's
            // not -> continue.
            Position anchor = Position.fromIndex(square);
            if (!board.isAnchor(anchor))
                continue;

            // 2. Create a buffer for easier backtracking
            currentRow = square / BoardConstants.SIZE;
            currentCol = square % BoardConstants.SIZE;

            char[] horiBuffer = horiLines[currentRow].clone();
            boolean[] horiPlaced = new boolean[BoardConstants.SIZE];

            char[] vertBuffer = vertLines[currentCol].clone();
            boolean[] vertPlaced = new boolean[BoardConstants.SIZE];

            // 3. Recursively start building words from the anchor in both vertical and
            // horizontal directions.
            reverseBuild(rack.clone(), horiBuffer, horiPlaced, currentCol, rack.length, true);
            reverseBuild(rack.clone(), vertBuffer, vertPlaced, currentRow, rack.length, false);

            // 4. If we found any moves for this anchor, stop here
            if (nextMoves != null && !nextMoves.isEmpty()) {
                this.square++; // advance to the next square for next call
                return;
            }
        }
    }

    private void reverseBuild(char[] rack, char[] buffer, boolean[] placed, int depth, int limit,
            boolean isHorizontal) {
        // Build forward from the anchor
        buildWord(trieRoot, rack, buffer, placed, depth, limit, isHorizontal);

        // If we've hit the edge of the board, stop
        if (depth < 0)
            return;

        // If the current square is already filled, go to the next square
        if (buffer[depth] != GameConstants.EMPTY_SQUARE) {
            reverseBuild(rack, buffer, placed, depth - 1, limit, isHorizontal);
            return;
        }

        // If we've placed all tiles, there's nothing more to do
        if (limit == 0)
            return;

        // Try adding one more letter before the anchor (in the backStep direction)
        for (int i = 0; i < rack.length; i++) {
            char tile = rack[i];

            // If the letter is a blank, try all possible letters
            if (tile == BagConstants.BLANK) {
                for (char c : BagConstants.TILE_DATA.keySet()) {
                    if (c == BagConstants.BLANK)
                        continue;
                    char lower = Character.toLowerCase(c);

                    // Check if placing this tile here would form a valid cross word
                    if (!isCrossWordValid(lower, depth, isHorizontal))
                        continue;

                    // Place the blank tile in the buffer
                    buffer[depth] = lower;
                    placed[depth] = true;

                    // Remove the blank from the rack
                    char[] newRack = new char[rack.length - 1];
                    for (int k = 0, j = 0; k < rack.length; k++) {
                        if (k == i)
                            continue;
                        newRack[j++] = rack[k];
                    }

                    // Build on to the left
                    reverseBuild(newRack, buffer, placed, depth - 1, limit - 1, isHorizontal);

                    // Backtrack
                    buffer[depth] = GameConstants.EMPTY_SQUARE; // Set it to EMPTY_SQUARE
                    placed[depth] = false;
                }
                // Continue here because we've tried all blank options
                continue;
            }

            // Build for a normal tile
            if (!isCrossWordValid(tile, depth, isHorizontal))
                continue;

            // Place the blank tile in the buffer
            buffer[depth] = tile;
            placed[depth] = true;

            // Remove the tile from the rack
            char[] newRack = new char[rack.length - 1];
            for (int k = 0, j = 0; k < rack.length; k++) {
                if (k == i)
                    continue;
                newRack[j++] = rack[k];
            }

            // Build on to the left
            reverseBuild(newRack, buffer, placed, depth - 1, limit - 1, isHorizontal);

            // Backtrack
            buffer[depth] = GameConstants.EMPTY_SQUARE;
            placed[depth] = false;
        }
    }

    // Builds all words starting from 'depth' in the buffer, using letters from
    // 'rack'
    // and following the Trie from 'node'
    private void buildWord(TrieNode node, char[] rack, char[] buffer, boolean[] placed, int depth, int limit,
            boolean isHorizontal) {
        // Current square out-of-bounds → stop recursion
        if (depth >= BoardConstants.SIZE)
            return;

        // If square has a fixed tile
        if (buffer[depth] != GameConstants.EMPTY_SQUARE) {
            char letter = buffer[depth];

            // Gets the child node for this letter, or returns if there is none
            Optional<TrieNode> child = node.getChild(letter);
            if (child.isEmpty())
                return;

            buildWord(child.get(), rack, buffer, placed, depth + 1, limit, isHorizontal);

            // Record moves ending at this node
            if (child.get().isWord)
                recordMove(buffer, placed, isHorizontal);

            return;
        }

        // If square is empty -> try rack tiles
        for (int i = 0; i < rack.length; i++) {
            char tile = rack[i];

            // Blank tile
            if (tile == BagConstants.BLANK) {
                // Try all possible letters for the blank
                for (char c : BagConstants.TILE_DATA.keySet()) {
                    if (c == BagConstants.BLANK)
                        continue;
                    char lower = Character.toLowerCase(c);

                    // Gets the child node for this letter, or returns if there is none
                    Optional<TrieNode> child = node.getChild(lower);
                    if (child.isEmpty())
                        continue;

                    // Check if we can place this letter here (cross word valid)
                    if (!isCrossWordValid(lower, depth, isHorizontal))
                        continue;

                    buffer[depth] = lower;
                    placed[depth] = true;

                    // Remove the blank from the rack
                    char[] newRack = new char[rack.length - 1];
                    for (int k = 0, j = 0; k < rack.length; k++) {
                        if (k == i)
                            continue;
                        newRack[j++] = rack[k];
                    }

                    buildWord(child.get(), newRack, buffer, placed, depth + 1, limit - 1, isHorizontal);

                    // Record moves ending at this node
                    if (child.get().isWord)
                        recordMove(buffer, placed, isHorizontal);

                    // Backtrack
                    buffer[depth] = GameConstants.EMPTY_SQUARE;
                    placed[depth] = false;
                }
                continue;
            }

            // Normal tile
            Optional<TrieNode> child = node.getChild(tile);
            if (child.isEmpty())
                continue;

            if (!isCrossWordValid(tile, depth, isHorizontal))
                continue;

            buffer[depth] = tile;
            placed[depth] = true;

            // Remove the blank from the rack
            char[] newRack = new char[rack.length - 1];
            for (int k = 0, j = 0; k < rack.length; k++) {
                if (k == i)
                    continue;
                newRack[j++] = rack[k];
            }

            buildWord(child.get(), newRack, buffer, placed, depth + 1, limit - 1, isHorizontal);

            // Record moves ending at this node
            if (child.get().isWord)
                recordMove(buffer, placed, isHorizontal);

            // Backtrack
            buffer[depth] = GameConstants.EMPTY_SQUARE;
            placed[depth] = false;
        }
    }

    // Check if placing 'letter' at 'depth' in the buffer (which corresponds to a
    // position on the board)
    // Check if placing 'letter' at 'depth' in the buffer (which corresponds to a
    // position on the board)
    private boolean isCrossWordValid(char letter, int depth, boolean isHorizontal) {
        // Get the correctly associated line (clone it because we will modify)
        char[] associatedLine = (isHorizontal ? vertLines[depth] : horiLines[depth]).clone();

        // The index inside associatedLine that corresponds to the place we're testing
        int pos = isHorizontal ? currentRow : currentCol;

        // Put the candidate letter into the cloned line
        associatedLine[pos] = letter;

        // Find start and end of the contiguous word that includes 'pos'
        int start = pos;
        while (start > 0 && associatedLine[start - 1] != GameConstants.EMPTY_SQUARE) {
            start--;
        }
        int end = pos;
        while (end < BoardConstants.SIZE - 1 && associatedLine[end + 1] != GameConstants.EMPTY_SQUARE) {
            end++;
        }

        int length = end - start + 1;
        if (length == 1) {
            // No adjacent tiles → no crossword formed
            return true;
        }

        String word = new String(associatedLine, start, length);
        return dictionary.isWord(word);
    }

    // Record a move found in the buffer, where 'placed' indicates which letters
    // were placed by us and which were already on the board
    private void recordMove(char[] buffer, boolean[] placed, boolean isHorizontal) {
        int start = 0, end = BoardConstants.SIZE - 1;

        // Find the span of the word (skip leading/trailing empty squares)
        while (start < BoardConstants.SIZE && buffer[start] == GameConstants.EMPTY_SQUARE) {
            start++;
        }
        while (end >= 0 && buffer[end] == GameConstants.EMPTY_SQUARE) {
            end--;
        }

        // No word found
        if (start > end) {
            return;
        }

        // Collect new tile placements
        List<Position> posList = new ArrayList<>();
        List<Character> charList = new ArrayList<>();

        for (int i = start; i <= end; i++) {
            if (placed[i]) {
                Position pos = isHorizontal
                        ? new Position(currentRow, i)
                        : new Position(i, currentCol);

                posList.add(pos);
                charList.add(buffer[i]);
            }
        }

        // If we didn’t place any new tiles, it’s not a legal move
        if (posList.isEmpty()) {
            return;
        }

        // Convert lists into arrays
        Position[] positions = posList.toArray(new Position[0]);
        char[] letters = new char[charList.size()];
        for (int i = 0; i < charList.size(); i++) {
            letters[i] = charList.get(i);
        }

        Move move = new Move(positions, letters);

        if (nextMoves == null) {
            nextMoves = new ArrayDeque<>();
        }
        nextMoves.push(move);
    }

    @Override
    public boolean hasNext() {
        return (nextMoves != null && !nextMoves.isEmpty());
    }

    @Override
    public Move next() {
        if (!hasNext())
            throw new NoSuchElementException("There are no more moves");
        Move move = nextMoves.pop();
        if (nextMoves.isEmpty())
            advance();
        return move;
    }
}
