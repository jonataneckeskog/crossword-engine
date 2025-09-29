package scrabble.engine;

import scrabble.core.*;
import scrabble.core.components.*;
import scrabble.rules.TrieDictionary;
import scrabble.rules.game.BoardConstants;
import scrabble.rules.TrieNode;

import java.util.Iterator;

public class LegalMoveIterator implements Iterator<Move> {
    // Fields
    private final TrieDictionary dictionary;
    private final Board board;
    private final char[] boardArray;
    private final char[] rack;
    private final boolean isFirstMove;
    private Move nextMove = null;

    // Temporary fiels, used for iteration
    private int square;
    private char[] tempRack;

    public LegalMoveIterator(PlayerView playerView, TrieDictionary dictionary) {
        this.dictionary = dictionary;
        board = playerView.getBoard();
        boardArray = board.getBoard();
        rack = playerView.getRack().getLetters();
        isFirstMove = playerView.isFirstMove();
        tempRack = rack.clone();
        square = isFirstMove ? BoardConstants.TOTAL_SIZE / 2 : 0;
        advance();
    }

    // "Advanced" starts now
    private void advance() {
        for (int square = this.square; square < BoardConstants.TOTAL_SIZE; square++) {
            // 1. Create a position from the square and check if it is an anchor. If it's
            // not -> continue.
            Position anchorPosition = Position.fromIndex(square);
            if (!isFirstMove && !board.isAnchor(anchorPosition))
                continue;

            // 2. Create a buffer for easier backtracking
            char[] buffer = new char[BoardConstants.SIZE];
            char[] rackCopy = tempRack.clone();
            TrieNode trieRoot = dictionary.getRoot();
            int depth = 0;

            // 3. Recursively start building words from the anchor in both vertical and
            // horizontal directions.
            buildWord(anchorPosition, trieRoot, rackCopy, buffer, depth, Position.Step.RIGHT);
            rackCopy = tempRack.clone(); // reset rack
            buildWord(anchorPosition, trieRoot, rackCopy, buffer, depth, Position.Step.DOWN);
        }
    }

    private void buildWord(
            Position pos, // current position on board (the anchor for the first iteration)
            TrieNode node, // current Trie node
            char[] rack, // letters available
            char[] buffer, // letters placed so far (in order along 'step' direction)
            int depth,
            Position.Step step // horizontal or vertical
    ) {
        /*
         * Purpose
         * -------
         * Recursively generate and validate all legal moves that pass through 'pos'
         * in direction 'step', using 'node' (current trie state), 'rack' (available
         * letters) and 'buffer' (letters placed so far along the main word).
         *
         * Key correctness requirements (things the algorithm must ensure)
         * ------------------------------------------------------------
         * 1) The generated word must be in the dictionary (Trie) when complete.
         * 2) For non-first moves, the move must place at least one new tile and
         * connect to existing tiles (i.e., touch board tiles).
         * 3) Any cross-words (perpendicular to 'step') formed by newly placed tiles
         * must themselves be valid dictionary words.
         * 4) You must not overwrite existing tiles with a different letter.
         * 5) Do not place more tiles than the rack has (consider blanks/wildcards).
         * 6) The whole word must lie inside the board bounds.
         *
         * Algorithm outline (implementation notes)
         * ---------------------------------------
         * 0) Pre-conditions:
         * - 'pos' is the starting square (anchor) for the *main* recursion.
         * - 'buffer' holds letters placed so far along the main word (left-to-right
         * or top-to-bottom depending on 'step'). 'depth' == buffer length.
         *
         * 1) Handle any fixed prefix that already exists *before* the anchor:
         * - If there are contiguous existing tiles immediately before 'pos'
         * (in the negative 'step' direction), those letters must be incorporated
         * into the word prefix and the Trie advanced accordingly BEFORE placing
         * new letters forward. (In practice this is done by walking backward
         * from 'pos' to collect the fixed prefix or by calling a separate
         * "extend-left" routine that builds allowed prefixes from the rack.)
         *
         * 2) Main-recursive generation:
         * - At each recursive call consider the current board square S (initially
         * 'pos'):
         * a) If S is off-board: stop recursion.
         * b) If S already contains a letter L:
         * - Check whether 'node' has child L. If no -> prune and return.
         * - Append L to buffer, advance 'node' to child(L), move to next square
         * (pos + step), recurse.
         * c) If S is empty:
         * - For each distinct letter X (including using blanks) in the rack:
         * i) Check whether 'node' has child X. If no -> skip X (prune).
         * ii) Form the perpendicular (cross) word that would result if X
         * were placed at S: gather contiguous letters before/after S
         * along perpendicular direction plus X in the middle.
         * iii) If the cross-word length > 1, verify it is in the dictionary
         * (or that the Trie contains that exact cross word).
         * If invalid -> skip X (prune).
         * iv) Temporarily consume X from rack, append X to buffer, advance
         * 'node' to child(X) and recurse to next square (pos + step).
         * v) Backtrack: restore rack and buffer.
         *
         * 3) Recording valid moves:
         * - At any point where 'node' marks the end of a valid word (node.isWord()):
         * and the buffer length >= game minimum (usually >= 2 unless dictionaries
         * differ) and the placement satisfies connectivity rules (see #2 above),
         * record the candidate Move using:
         * - start position = the position corresponding to the first letter
         * in 'buffer' (this may be before the original anchor if you
         * generated a left-extended prefix),
         * - direction = step,
         * - letters placed with their board indices,
         * - used rack letters (including blanks mapped to actual letters).
         *
         * 4) Pruning and limits:
         * - Stop recursion when buffer length equals (existing fixed-prefix + rack
         * size).
         * - Use the Trie to prune early when no child nodes exist.
         * - Use precomputed cross-check sets for each square (optional) to speed up
         * step 2.c.iii by testing letter legality without building the whole cross
         * word.
         *
         * 5) First-move special case:
         * - For the first move the placed word must cover the center square.
         * Enforce that when recording candidate moves (or by only starting at
         * center in advance()).
         *
         * 6) Additional bookkeeping (optional but useful for scoring & tie-breaking):
         * - Track whether at least one tile was newly placed (move is legal only if
         * yes,
         * except if your rules permit passes).
         * - Track used tile indices and score incrementally for quick ranking.
         *
         * Implementation tip / recommended structure:
         * -------------------------------------------
         * - Implement two helper routines:
         * 1) extendLeftAndGenerate(pos, node, rack, partialPrefix, step):
         * - Generate all possible fixed prefixes that can be placed to the left
         * of 'pos' (including the empty prefix), using up to rack size letters.
         * - For each prefix produced, call extendRightFrom(startPos, nodeForPrefix,
         * rackAfterPrefix, bufferStartingWithPrefix, step).
         *
         * 2) extendRightFrom(currPos, node, rack, buffer, step):
         * - The recursive routine described above that continues forward and
         * collects complete words, checking cross-words and boundaries.
         *
         * - This two-phase approach (build prefix, then extend forward) matches
         * canonical Scrabble move-generation algorithms and ensures you don't
         * miss words that place tiles both before and after the anchor.
         *
         * Summary:
         * - The original comment was fine at a high level but must explicitly handle:
         * * any fixed prefix before the anchor,
         * * cross-word validation for each newly placed tile (including multi-letter
         * crosses),
         * * rack consumption including blanks, and
         * * ensuring connectivity and at-least-one-new-tile constraints.
         */
    }

    @Override
    public boolean hasNext() {
        return nextMove != null;
    }

    @Override
    public Move next() {
        Move move = nextMove;
        advance();
        return move;
    }

}
