package scrabble.engine;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import scrabble.core.GameState;
import scrabble.core.PlayerView;
import scrabble.rules.TrieDictionary;
import scrabble.core.Move;
import scrabble.rules.game.*;

import java.util.List;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class LegalMoveIteratorTest {

    private PlayerView playerView;
    private LegalMoveIterator legalMoveIterator;

    @BeforeEach
    void setup() {
        // Start a new empty game
        String boardString = String.valueOf(GameConstants.EMPTY_SQUARE).repeat(BoardConstants.TOTAL_SIZE);
        String bagToFirstMove = "XORANDIFELSE/AND/NAND/100/100";
        String gameString = boardString + "/" + bagToFirstMove;

        GameState gameState = GameState.stateFrom(gameString);
        playerView = PlayerView.fromGameState(gameState, 0);

        // Create a simple dictionary containing a single word
        List<String> words = new ArrayList<>();
        words.add("and");
        TrieDictionary dictionary = new TrieDictionary(words);

        // Create the move generator
        legalMoveIterator = new LegalMoveIterator(playerView, dictionary);
    }

    @Test
    void testFirstMoveGeneratesCenterWord() {
        // Ensure there is at least one legal move
        assertTrue(legalMoveIterator.hasNext(), "The move generator should find at least one move");

        List<Move> moves = new ArrayList<>();

        // Check that each move covers the center (first move rule)
        while (legalMoveIterator.hasNext()) {
            Move move = legalMoveIterator.next();
            moves.add(move);
            boolean coversCenter = false;
            for (int i = 0; i < move.getPositions().length; i++) {
                if (move.getPositions()[i].equals(BoardConstants.TOTAL_SIZE / 2)) {
                    coversCenter = true;
                    break;
                }
            }
            assertTrue(coversCenter, "First move must cover the center square");
        }

        // Optional: check that letters placed are from rack
        for (Move move : moves) {
            for (char c : move.getTiles()) {
                assertTrue(playerView.getRack().hasLetter(c),
                        "Letters placed in first move should come from the player's rack");
            }
        }
    }

    @Test
    void testSecondMoveConnectsToExistingTile() {
        // First move
        if (legalMoveIterator.hasNext()) {
            Move move = legalMoveIterator.next();

            // Apply first move to the board (simulate)
            playerView = playerView.applyMove(move);

            // Create a new iterator for second move
            legalMoveIterator = new LegalMoveIterator(playerView, new TrieDictionary(List.of("and", "an", "dan")));

            assertTrue(legalMoveIterator.hasNext(), "Second move generator should find legal moves");

            // Check that each second move touches at least one existing tile
            while (legalMoveIterator.hasNext()) {
                Move secondMove = legalMoveIterator.next();
                boolean touchesExisting = false;
                for (int i = 0; i < secondMove.getPositions().length; i++) {
                    if (!playerView.getBoard().isEmpty(secondMove.getPositions()[i])) {
                        touchesExisting = true;
                        break;
                    }
                }
                assertTrue(touchesExisting, "Second move must connect to existing tiles");
            }
        }
    }
}