package scrabble.engine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import scrabble.core.*;
import scrabble.rules.MoveValidator;
import scrabble.rules.TrieDictionary;
import scrabble.rules.game.BoardConstants;
import scrabble.rules.game.GameConstants;
import scrabble.rules.game.GameRules;

public class EvaluatingEngineTest {
    private GameState gameState;
    private PlayerView playerView;
    private MoveGenerator moveGenerator;
    private TrieDictionary dictionary;
    private Evaluator evaluator;
    private EvaluatingEngine engine;
    private SearchListener listener;

    @BeforeAll
    static void initialSetup() {
        GameRules.load("src/main/resources/scrabble.json"); // or correct path
    }

    @BeforeEach
    void setup() {
        String boardString = String.valueOf(GameConstants.EMPTY_SQUARE).repeat(BoardConstants.TOTAL_SIZE);
        String bagToFirstMove = "XORANDIFELSE/STRINGS/AD/100/100";
        String gameString = boardString + "/" + bagToFirstMove;

        gameState = GameState.stateFrom(gameString);
        playerView = PlayerView.fromGameState(gameState, 0);

        List<String> words = new ArrayList<>();
        words.add("SING");
        words.add("RING");
        words.add("RINGS");
        words.add("STRING");
        words.add("STRINGS");
        dictionary = new TrieDictionary(words);

        moveGenerator = new MoveGenerator(dictionary);
        evaluator = new StandardEvaluator();

        engine = new EvaluatingEngine(moveGenerator, evaluator);

        listener = new SearchListener();
    }

    @Test
    void testGetBestMove() {
        Move move = engine.chooseMove(playerView, 500);
        assertEquals(7, move.getPositions().length); // STRINGS
    }

    @Test
    void testGeneratedMovesAreValid() throws InterruptedException {
        // Start the search in its own thread
        engine.search(playerView, listener);

        // Let the engine search for ~500ms
        Thread.sleep(500);

        // Stop the engine (waits for thread to finish)
        engine.stop();

        // Validate generated moves
        MoveValidator moveValidator = new MoveValidator(dictionary);
        for (Move move : listener.getMoves().keySet()) {
            assertTrue(moveValidator.isValid(playerView.getBoard(), move),
                    "Invalid move: " + move);
            assertTrue(listener.getMoves().get(move) >= 0,
                    "Move evaluation was negative: " + move);
        }
    }
}
