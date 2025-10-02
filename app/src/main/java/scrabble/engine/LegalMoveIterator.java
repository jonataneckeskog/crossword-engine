package scrabble.engine;

import scrabble.core.*;
import scrabble.core.components.*;
import scrabble.rules.TrieDictionary;
import scrabble.rules.game.BoardConstants;
import scrabble.rules.TrieNode;

import java.util.List;
import java.util.ArrayList;
import java.util.Deque;
import java.util.ArrayDeque;
import java.util.NoSuchElementException;
import java.util.Iterator;
import java.util.Optional;

public class LegalMoveIterator implements Iterator<Move> {
    // Fields
    private final TrieDictionary dictionary;
    private final Board board;
    private final char[] rack;
    private final boolean isFirstMove;

    // Iterator variable
    private Deque<Move> nextMoves = null;

    // Temporary fiels, used for iteration
    private int square;

    public LegalMoveIterator(PlayerView playerView, TrieDictionary dictionary) {
        this.dictionary = dictionary;
        board = playerView.getBoard();
        rack = playerView.getRack().getLetters();
        isFirstMove = playerView.isFirstMove();
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
            char[] rackCopy = rack.clone();
            TrieNode trieRoot = dictionary.getRoot();
            int depth = 0;

            reverseBuild(anchorPosition, trieRoot, rackCopy, buffer, placed, depth, Position.Step.LEFT, rack.length);
            rackCopy = rack.clone(); // reset rack
            reverseBuild(anchorPosition, trieRoot, rackCopy, buffer, placed, depth, Position.Step.UP, rack.length);

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
            char[] rackCopy = rack.clone();
            TrieNode trieRoot = dictionary.getRoot();
            int depth = 0;

            // 3. Recursively start building words from the anchor in both vertical and
            // horizontal directions.
            reverseBuild(anchorPosition, trieRoot, rackCopy, buffer, placed, depth, Position.Step.LEFT, rack.length);
            rackCopy = rack.clone(); // reset rack
            reverseBuild(anchorPosition, trieRoot, rackCopy, buffer, placed, depth, Position.Step.UP, rack.length);

            // 4. If we found any moves for this anchor, stop here
            if (nextMoves != null && !nextMoves.isEmpty()) {
                this.square++; // advance to the next square for next call
                return;
            }
        }
    }

    // --- reverseBuild (fixed) ---
    private void reverseBuild(Position anchor, TrieNode node,
            char[] rack, char[] buffer, boolean[] placed,
            int depth, Position.Step backStep, int limit) {

        // build forward from the anchor using the opposite of 'backStep'
        Position.Step forwardStep = Position.Step.reverseStep(backStep);
        buildWord(anchor, node, rack, buffer, placed, depth, forwardStep);

        if (limit == 0)
            return;

        // Try adding one more letter before the anchor (in the backStep direction)
        for (int i = 0; i < rack.length; i++) {
            char tile = rack[i];
            Optional<TrieNode> child = node.getChild(tile);
            if (child.isEmpty())
                continue;

            // Add tile to buffer at current depth
            buffer[depth] = tile;
            placed[depth] = true;

            // Remove a tile from the rack — copy all except index i
            char[] newRack = new char[rack.length - 1];
            for (int k = 0, j = 0; k < rack.length; k++) {
                if (k == i)
                    continue;
                newRack[j++] = rack[k];
            }

            // Step backwards on the board (one square further away from the anchor)
            Position prev = anchor.step(backStep);

            // Recurse deeper (add longer prefix). Note we keep backStep the same.
            reverseBuild(prev, child.get(), newRack, buffer, placed, depth + 1, backStep, limit - 1);

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
                recordMove(pos, step, buffer, placed, depth + 1);

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
                recordMove(pos, step, buffer, placed, depth + 1);
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

    // --- recordMove (fixed) ---
    // Now `step` is the forward direction: from word start --> word end
    private void recordMove(Position endPos, Position.Step step,
            char[] buffer, boolean[] placed, int depth) {

        // Compute start by stepping from endPos towards the start (reverse of forward
        // step)
        Position.Step backwards = Position.Step.reverseStep(step);
        Position start = endPos;
        for (int i = 1; i < depth; i++) {
            start = start.step(backwards);
        }

        // Walk forwards from start to collect placed positions and letters
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
            p = p.step(step); // move forward (start -> end)
        }

        if (!placedAtLeastOne)
            return;

        Position[] positions = positionsList.toArray(new Position[0]);
        char[] letters = new char[lettersList.size()];
        for (int i = 0; i < letters.length; i++) {
            letters[i] = lettersList.get(i);
        }

        if (nextMoves == null) {
            nextMoves = new ArrayDeque<>();
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
        Move move = nextMoves.pop();
        if (nextMoves.isEmpty())
            advance();
        return move;
    }
}
