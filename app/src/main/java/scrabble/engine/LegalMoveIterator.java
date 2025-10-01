package scrabble.engine;

import scrabble.core.*;
import scrabble.core.components.*;
import scrabble.rules.TrieDictionary;
import scrabble.rules.game.BoardConstants;
import scrabble.rules.TrieNode;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Optional;

public class LegalMoveIterator implements Iterator<Move> {
    // Fields
    private final TrieDictionary dictionary;
    private final Board board;
    private final char[] rack;
    private final boolean isFirstMove;

    // Iterator variable
    private List<Move> nextMoves = null;

    // Temporary fiels, used for iteration
    private int square;
    private char[] tempRack;

    public LegalMoveIterator(PlayerView playerView, TrieDictionary dictionary) {
        this.dictionary = dictionary;
        board = playerView.getBoard();
        rack = playerView.getRack().getLetters();
        isFirstMove = playerView.isFirstMove();
        tempRack = rack.clone();
        square = 0;
        advance();
    }

    // "Advanced" starts now
    private void advance() {
        nextMoves = null;

        if (isFirstMove) {
            Position anchorPosition = Position.fromIndex(square);
            char[] buffer = new char[BoardConstants.SIZE];
            boolean[] placed = new boolean[BoardConstants.SIZE];
            char[] rackCopy = tempRack.clone();
            TrieNode trieRoot = dictionary.getRoot();
            int depth = 0;

            buildWord(anchorPosition, trieRoot, rackCopy, buffer, placed, depth, Position.Step.RIGHT);
            rackCopy = tempRack.clone(); // reset rack
            buildWord(anchorPosition, trieRoot, rackCopy, buffer, placed, depth, Position.Step.DOWN);

            return;
        }

        for (; this.square < BoardConstants.TOTAL_SIZE; this.square++) {
            // 1. Create a position from the square and check if it is an anchor. If it's
            // not -> continue.
            Position anchorPosition = Position.fromIndex(square);
            if (!board.isAnchor(anchorPosition))
                continue;

            // 2. Create a buffer for easier backtracking
            char[] buffer = new char[BoardConstants.SIZE];
            boolean[] placed = new boolean[BoardConstants.SIZE];
            char[] rackCopy = tempRack.clone();
            TrieNode trieRoot = dictionary.getRoot();
            int depth = 0;

            // 3. Recursively start building words from the anchor in both vertical and
            // horizontal directions.
            reverseBuild(anchorPosition, trieRoot, rackCopy, buffer, placed, depth, Position.Step.LEFT, rack.length);
            rackCopy = tempRack.clone(); // reset rack
            reverseBuild(anchorPosition, trieRoot, rackCopy, buffer, placed, depth, Position.Step.UP, rack.length);

            // 4. If we found any moves for this anchor, stop here
            if (nextMoves != null && !nextMoves.isEmpty()) {
                this.square++; // advance to the next square for next call
                return;
            }
        }
    }

    private void reverseBuild(Position anchor, TrieNode node,
            char[] rack, char[] buffer, boolean[] placed,
            int depth, Position.Step step, int limit) {
        // 1) Once we stop extending backwards, start normal forward building
        buildWord(anchor, node, rack, buffer, placed, depth, step);

        // 2) If we’ve used all rack letters, stop
        if (limit == 0)
            return;

        // 3) Try adding one more letter before the anchor
        for (int i = 0; i < rack.length; i++) {
            char tile = rack[i];
            Optional<TrieNode> child = node.getChild(tile);
            if (child.isEmpty())
                continue;

            // Add tile to buffer at current depth
            buffer[depth] = tile;
            placed[depth] = true;

            // Remove a tile from the rack
            char[] newRack = new char[limit - 1];
            for (int u = 0; u < limit - 1; u++) {
                if (u != i) {
                    newRack[u] = rack[i];
                }
            }

            // Step backwards on the board
            Position prev = anchor.step(step);

            // Recurse deeper (add longer prefix)
            reverseBuild(prev, child.get(), newRack, buffer, placed, depth + 1, Position.Step.reverseStep(step),
                    limit - 1);

            placed[depth] = false; // backtrack
        }
    }

    private void buildWord(
            Position pos, // current position on board (the anchor for the first iteration)
            TrieNode node, // current Trie node
            char[] rack, // letters available
            char[] buffer, // letters placed so far (in order along 'step' direction)
            boolean[] placed,
            int depth,
            Position.Step step // horizontal or vertical
    ) {
        // 1. Current square out-of-bounds → stop recursion
        if (board.isOutOfBounds(pos))
            return;

        // 2. If square has a fixed tile
        if (!board.isEmpty(pos)) {
            char letter = board.tileAt(pos);
            Optional<TrieNode> child = node.getChild(letter);
            if (child.isEmpty())
                return;

            buffer[depth] = letter;
            placed[depth] = false;
            buildWord(pos.step(step), child.get(), rack, buffer, placed, depth + 1, step);

            // record moves ending at this node
            if (child.get().isWord)
                recordMove(pos, Position.Step.reverseStep(step), buffer, placed, depth + 1);

            return;
        }

        // 3. If square is empty → try rack tiles
        for (int i = 0; i < rack.length; i++) {
            char tile = rack[i];
            Optional<TrieNode> child = node.getChild(tile);
            if (child.isEmpty())
                continue;
            if (!isCrossWordValid(pos, tile, Position.Step.otherStep(step)))
                continue;

            buffer[depth] = tile;
            placed[depth] = true;

            // rack is a char[] containing all available tiles
            char[] newRack = new char[rack.length - 1];
            for (int k = 0, j = 0; k < rack.length; k++) {
                if (k == i)
                    continue; // skip the tile we just used
                newRack[j++] = rack[k]; // copy the rest
            }

            buildWord(pos.step(step), child.get(), newRack, buffer, placed, depth + 1, step);

            placed[depth] = false;

            // record moves ending at this node
            if (child.get().isWord)
                recordMove(pos, Position.Step.reverseStep(step), buffer, placed, depth + 1);
        }
    }

    private boolean isCrossWordValid(Position pos, char tile, Position.Step perpStep) {
        Position.Step reverseStep = Position.Step.reverseStep(perpStep);

        // 1) Find start of the cross word
        Position start = pos;
        while (!board.isOutOfBounds(start.step(reverseStep)) && !board.isEmpty(start.step(reverseStep))) {
            start = start.step(reverseStep);
        }

        // 2) Build the word
        StringBuilder sb = new StringBuilder();
        Position current = start;
        while (!board.isOutOfBounds(current) && (!board.isEmpty(current) || current.equals(pos))) {
            if (current.equals(pos)) {
                sb.append(tile); // include candidate tile
            } else {
                sb.append(board.tileAt(current));
            }
            current = current.step(perpStep);
        }

        // 3) If the cross word has length 1, no new word is formed -> valid
        if (sb.length() == 1)
            return true;

        // 4) Validate the word
        return dictionary.isWord(sb.toString());
    }

    private void recordMove(Position endPos, Position.Step step,
            char[] buffer, boolean[] placed, int depth) {
        // Figure out where the word actually starts
        Position start = endPos;
        for (int i = 1; i < depth; i++) {
            start = start.step(step);
        }

        // Walk through the buffer along the board
        List<Position> positionsList = new ArrayList<>();
        List<Character> lettersList = new ArrayList<>();

        Position p = start;
        boolean placedAtLeastOne = false;

        for (int i = 0; i < depth; i++) {
            if (placed[i]) {
                positionsList.add(p);
                lettersList.add(buffer[i]);
                placedAtLeastOne = true;
            }
            p = p.step(step);
        }

        // Must place at least one tile
        if (!placedAtLeastOne)
            return;

        // Convert to arrays
        Position[] positions = positionsList.toArray(new Position[0]);
        char[] letters = new char[lettersList.size()];
        for (int i = 0; i < letters.length; i++) {
            letters[i] = lettersList.get(i);
        }

        if (nextMoves == null) {
            nextMoves = new ArrayList<>();
        }
        nextMoves.add(new Move(positions, letters));
    }

    @Override
    public boolean hasNext() {
        return nextMoves != null;
    }

    @Override
    public Move next() {
        if (!hasNext())
            throw new NoSuchElementException("There are no more moves");
        Move move = nextMoves.removeLast();
        if (nextMoves.isEmpty())
            advance();
        return move;
    }
}
