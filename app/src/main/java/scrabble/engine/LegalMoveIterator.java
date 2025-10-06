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
    private final TrieDictionary dictionary;
    private final Board board;
    private final char[] rack;
    private final boolean isFirstMove;
    private final TrieNode trieRoot;

    // Iterator variable
    private Deque<Move> nextMoves = null;
    private boolean firstMoveProcessed = false;
    private boolean[] triedAnchors;

    // Temporary fiels, used for iteration
    private int square;
    private int currentRow;
    private int currentCol;
    private boolean recordedSingleMoveHorizontaly;

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
        this.board = playerView.getBoard();
        this.rack = playerView.getRack().getLetters();
        this.isFirstMove = playerView.isFirstMove();
        this.trieRoot = dictionary.getRoot();
        triedAnchors = new boolean[BoardConstants.TOTAL_SIZE];
        square = 0;
        initializeBuffers();
        advance();
    }

    // "Advanced" starts now
    private void advance() {
        nextMoves = null;

        if (isFirstMove) {
            if (firstMoveProcessed)
                return;

            recordedSingleMoveHorizontaly = false;

            // Special case: first move must cover center square
            currentRow = BoardConstants.SIZE / 2;
            currentCol = BoardConstants.SIZE / 2;

            char[] horiBuffer = horiLines[currentRow].clone();
            boolean[] horiPlaced = new boolean[BoardConstants.SIZE];

            char[] vertBuffer = vertLines[currentCol].clone();
            boolean[] vertPlaced = new boolean[BoardConstants.SIZE];

            reverseBuild(rack.clone(), horiBuffer, horiPlaced, currentCol, rack.length, true);
            reverseBuild(rack.clone(), vertBuffer, vertPlaced, currentRow, rack.length, false);

            firstMoveProcessed = true;
            return;
        }

        for (; this.square < BoardConstants.TOTAL_SIZE; this.square++) {
            // 1. Create a position from the square and check if it is an anchor. If it's
            // not -> continue.
            Position anchor = Position.fromIndex(square);
            if (!board.isAnchor(anchor))
                continue;

            recordedSingleMoveHorizontaly = false;

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

            // 4. Mark the anchor square as tried
            triedAnchors[square] = true;

            // 5. If we found any moves for this anchor, stop here
            if (nextMoves != null && !nextMoves.isEmpty()) {
                this.square++; // advance to the next square for next call
                return;
            }
        }
    }

    private void reverseBuild(char[] rack, char[] buffer, boolean[] placed, int depth, int limit,
            boolean isHorizontal) {
        if (depth < 0)
            return;

        if (limit == 0)
            return;

        // If we're at an anready explored anchor, abort mission movegeneration
        int r = isHorizontal ? currentRow : depth;
        int c = isHorizontal ? depth : currentCol;
        if (triedAnchors[r * BoardConstants.SIZE + c])
            return;

        // Calculate starting pos
        int realStart = depth;
        while (realStart > 0 && buffer[realStart - 1] != GameConstants.EMPTY_SQUARE) {
            realStart--;
        }

        // If tile is filled
        if (buffer[depth] != GameConstants.EMPTY_SQUARE) {
            if (realStart == depth) { // If we happen to already be at the start of a word
                if (realStart > 0)
                    realStart--;
                else
                    return;
            }
            // Build one square back
            reverseBuild(rack, buffer, placed, realStart, limit, isHorizontal);
        }

        // If tile is empty
        else {
            // Try to place all letters in the original position
            boolean[] triedLetters = new boolean[BagConstants.UNIQUE_TILES];
            for (int i = 0; i < rack.length; i++) {
                char tile = rack[i];

                // Mark the tile as tried
                int tileIndex = BagConstants.getIndex(tile);
                if (triedLetters[tileIndex])
                    continue;
                triedLetters[tileIndex] = true;

                if (tile == BagConstants.BLANK) {
                    for (int u = 0; u < BagConstants.UNIQUE_TILES; u++) {
                        char blank = BagConstants.INDEX_TO_CHAR[u];
                        char lower = Character.toLowerCase(blank);

                        // Build for a normal tile
                        if (!isCrossWordValid(blank, depth, isHorizontal))
                            continue;

                        // Place the blank tile in the buffer
                        buffer[depth] = lower;
                        placed[depth] = true;

                        // Remove the tile from the rack
                        char[] newRack = new char[rack.length - 1];
                        for (int k = 0, j = 0; k < rack.length; k++) {
                            if (k == i)
                                continue;
                            newRack[j++] = rack[k];
                        }

                        // Build Da Word
                        buildWord(trieRoot, newRack, buffer, placed, realStart, limit - 1, isHorizontal);

                        // Build on to the left using the real depth
                        reverseBuild(newRack, buffer, placed, realStart, limit - 1, isHorizontal);

                        // Backtrack
                        buffer[depth] = GameConstants.EMPTY_SQUARE;
                        placed[depth] = false;
                    }
                    return;
                }

                // Build for a normal tile
                if (!isCrossWordValid(tile, depth, isHorizontal))
                    continue;

                // Place the tile in the buffer
                buffer[depth] = tile;
                placed[depth] = true;

                // Remove the tile from the rack
                char[] newRack = new char[rack.length - 1];
                for (int k = 0, j = 0; k < rack.length; k++) {
                    if (k == i)
                        continue;
                    newRack[j++] = rack[k];
                }

                // Build Da Word
                buildWord(trieRoot, newRack, buffer, placed, realStart, limit - 1, isHorizontal);

                // Build on to the left
                reverseBuild(newRack, buffer, placed, realStart, limit - 1, isHorizontal);

                // Backtrack
                buffer[depth] = GameConstants.EMPTY_SQUARE;
                placed[depth] = false;
            }
            return;
        }
    }

    private void buildWord(TrieNode node, char[] rack, char[] buffer, boolean[] placed, int depth, int limit,
            boolean isHorizontal) {
        if (depth >= BoardConstants.SIZE)
            return;

        // If the square is full, just continue
        if (buffer[depth] != GameConstants.EMPTY_SQUARE) {
            Optional<TrieNode> child = node.getChild(buffer[depth]);
            if (child.isEmpty())
                return;

            buildWord(child.get(), rack, buffer, placed, depth + 1, limit, isHorizontal);
            return;
        }

        // Record move
        boolean recordCondition;
        if (this.rack.length - limit == 1) {
            if (recordedSingleMoveHorizontaly) {
                if (isHorizontal) {
                    if (!node.isWord) {
                        char[] lineToValidate = vertLines[currentCol].clone();
                        lineToValidate[currentRow] = buffer[currentCol];
                        recordCondition = isWordValid(lineToValidate, currentRow, false);
                        recordedSingleMoveHorizontaly = recordCondition;
                    } else {
                        recordCondition = true;
                    }
                } else {
                    recordCondition = false;
                }
            } else {
                if (!isHorizontal) {
                    if (!node.isWord) {
                        char[] lineToValidate = horiLines[currentRow].clone();
                        lineToValidate[currentCol] = buffer[currentRow];
                        recordCondition = isWordValid(lineToValidate, currentCol, false);
                        recordedSingleMoveHorizontaly = recordCondition;
                    } else {
                        recordCondition = true;
                    }
                } else {
                    recordCondition = false;
                }
            }
        } else {
            recordCondition = node.isWord;
        }
        if (recordCondition)
            recordMove(buffer, placed, isHorizontal);

        if (limit == 0)
            return;

        // Try new letters and keep building
        boolean[] triedLetters = new boolean[BagConstants.UNIQUE_TILES];
        for (int i = 0; i < limit; i++) {
            char tile = rack[i];

            // Mark the tile as tried
            int tileIndex = BagConstants.getIndex(tile);
            if (triedLetters[tileIndex])
                continue;
            triedLetters[tileIndex] = true;

            if (tile == BagConstants.BLANK) {
                for (int u = 0; u < BagConstants.UNIQUE_TILES; u++) {
                    char blank = BagConstants.INDEX_TO_CHAR[u];
                    char lower = Character.toLowerCase(blank);

                    // Treat the blank as a normal tile
                    Optional<TrieNode> child = node.getChild(blank);
                    if (child.isEmpty())
                        continue;

                    if (!isCrossWordValid(blank, depth, isHorizontal))
                        continue;

                    buffer[depth] = lower;
                    placed[depth] = true;

                    // Remove the tile from the rack
                    char[] newRack = new char[rack.length - 1];
                    for (int k = 0, j = 0; k < rack.length; k++) {
                        if (k == i)
                            continue;
                        newRack[j++] = rack[k];
                    }

                    buildWord(child.get(), newRack, buffer, placed, depth + 1, limit - 1, isHorizontal);

                    // Backtrack
                    buffer[depth] = GameConstants.EMPTY_SQUARE;
                    placed[depth] = false;
                }
                return;
            }

            // Normal tile
            Optional<TrieNode> child = node.getChild(tile);
            if (child.isEmpty())
                continue;

            if (!isCrossWordValid(tile, depth, isHorizontal))
                continue;

            buffer[depth] = tile;
            placed[depth] = true;

            // Remove the tile from the rack
            char[] newRack = new char[rack.length - 1];
            for (int k = 0, j = 0; k < rack.length; k++) {
                if (k == i)
                    continue;
                newRack[j++] = rack[k];
            }

            buildWord(child.get(), newRack, buffer, placed, depth + 1, limit - 1, isHorizontal);

            // Backtrack
            buffer[depth] = GameConstants.EMPTY_SQUARE;
            placed[depth] = false;
        }
    }

    // Check if placing letter at depth in the buffer (which corresponds to a
    // position on the board)
    private boolean isCrossWordValid(char letter, int depth, boolean isHorizontal) {
        // Get the correctly associated line (clone it because we will modify)
        char[] associatedLine = (isHorizontal ? vertLines[depth] : horiLines[depth]).clone();

        // The index inside associatedLine that corresponds to the place we're testing
        int pos = isHorizontal ? currentRow : currentCol;

        // Put the candidate letter into the cloned line
        associatedLine[pos] = letter;

        return isWordValid(associatedLine, pos, true);
    }

    // Check if a word is valid once it's already placed in the buffer
    private boolean isWordValid(char[] buffer, int depth, boolean allow1s) {
        // Find start and end of the contiguous word that includes 'pos'
        int start = depth;
        while (start > 0 && buffer[start - 1] != GameConstants.EMPTY_SQUARE) {
            start--;
        }
        int end = depth;
        while (end < BoardConstants.SIZE - 1 && buffer[end + 1] != GameConstants.EMPTY_SQUARE) {
            end++;
        }

        int length = end - start + 1;
        if (allow1s && length == 1) {
            // No adjacent tiles → no crossword formed
            return true;
        }

        String word = new String(buffer, start, length);
        return dictionary.isWord(word);
    }

    // Record a move found in the buffer, where 'placed' indicates which letters
    // were placed by us and which were already on the board
    private void recordMove(char[] buffer, boolean[] placed, boolean isHorizontal) {
        // 1) find min/max indices where we placed tiles
        int minPlaced = -1, maxPlaced = -1;
        for (int i = 0; i < BoardConstants.SIZE; i++) {
            if (placed[i]) {
                if (minPlaced == -1)
                    minPlaced = i;
                maxPlaced = i;
            }
        }
        if (minPlaced == -1)
            return; // nothing placed

        // 2) expand to include contiguous existing tiles adjacent to placed tiles
        int start = minPlaced;
        while (start > 0 && buffer[start - 1] != GameConstants.EMPTY_SQUARE)
            start--;
        int end = maxPlaced;
        while (end < BoardConstants.SIZE - 1 && buffer[end + 1] != GameConstants.EMPTY_SQUARE)
            end++;

        // 3) collect only the positions/letters we actually placed
        List<Position> posList = new ArrayList<>();
        List<Character> charList = new ArrayList<>();
        for (int i = start; i <= end; i++) {
            if (placed[i]) {
                Position pos = isHorizontal ? new Position(currentRow, i) : new Position(i, currentCol);
                posList.add(pos);
                charList.add(buffer[i]);
            }
        }
        if (posList.isEmpty())
            return;

        Position[] positions = posList.toArray(new Position[0]);
        char[] letters = new char[charList.size()];
        for (int i = 0; i < letters.length; i++)
            letters[i] = charList.get(i);

        Move move = new Move(positions, letters);
        if (nextMoves == null)
            nextMoves = new ArrayDeque<>();

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
