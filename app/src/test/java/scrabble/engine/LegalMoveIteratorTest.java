package scrabble.engine;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import scrabble.core.GameState;
import scrabble.core.PlayerView;
import scrabble.core.Position;
import scrabble.rules.MoveValidator;
import scrabble.rules.TrieDictionary;
import scrabble.core.Move;
import scrabble.rules.game.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class LegalMoveIteratorTest {
    private GameState gameState;
    private PlayerView playerView;
    private LegalMoveIterator legalMoveIterator;

    @BeforeAll
    static void initialSetup() {
        GameRules.load("src/main/resources/scrabble.json");
    }

    @BeforeEach
    void setup() {
        // Start a new empty game
        String boardString = String.valueOf(GameConstants.EMPTY_SQUARE).repeat(BoardConstants.TOTAL_SIZE);
        String bagToFirstMove = "XORANDIFELSE/AND/AD/100/100";
        String gameString = boardString + "/" + bagToFirstMove;

        gameState = GameState.stateFrom(gameString);
        playerView = PlayerView.fromGameState(gameState, 0);

        // Create a simple dictionary containing a single word
        List<String> words = new ArrayList<>();
        words.add("AND"); // use uppercase for consistency with rack/bag
        TrieDictionary dictionary = new TrieDictionary(words);

        // Create the move generator
        legalMoveIterator = new LegalMoveIterator(playerView, dictionary);
    }

    @Test
    void testGenerateAllMoves() {
        List<Move> moves = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            assertTrue(legalMoveIterator.hasNext());
            moves.add(legalMoveIterator.next());
        }

        Set<Move> moveSet = new HashSet<>(moves);

        assertEquals(6, moveSet.size());
        assertEquals(6, moves.size());
    }

    @Test
    void testDuplicateSingleLetterMove() {
        String boardString = String.valueOf(GameConstants.EMPTY_SQUARE).repeat(BoardConstants.TOTAL_SIZE);
        String bagToFirstMove = "XORANDIFELSE/A/AD/100/100";
        String gameString = boardString + "/" + bagToFirstMove;

        GameState gameState = GameState.stateFrom(gameString);
        PlayerView playerView = PlayerView.fromGameState(gameState, 0);

        // Create a simple dictionary containing a single word
        List<String> words = new ArrayList<>();
        words.add("A");
        TrieDictionary dictionary = new TrieDictionary(words);

        LegalMoveIterator newIterator = new LegalMoveIterator(playerView, dictionary);
        MoveValidator moveValidator = new MoveValidator(dictionary);

        assertTrue(newIterator.hasNext());

        List<Move> moves = new ArrayList<>();
        while (newIterator.hasNext()) {
            Move move = newIterator.next();
            assertTrue(moveValidator.isValid(playerView.getBoard(), move),
                    "Invalid move: " + move);
            assertFalse(moves.contains(move));
            moves.add(move);
        }
    }

    @Test
    void testFirstMoveGeneratesCenterWord() {
        List<Move> moves = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            assertTrue(legalMoveIterator.hasNext());
            moves.add(legalMoveIterator.next());
        }

        // Check that each move covers the center (first move rule)
        Position center = Position.fromIndex(BoardConstants.TOTAL_SIZE / 2);
        for (Move move : moves) {
            boolean coversCenter = false;
            for (Position pos : move.getPositions()) {
                if (pos.equals(center)) {
                    coversCenter = true;
                    break;
                }
            }
            assertTrue(coversCenter, "First move must cover the center square");
        }

        // Check that letters placed are from the player's rack
        for (Move move : moves) {
            for (char c : move.getTiles()) {
                assertTrue(playerView.getRack().hasLetter(c),
                        "Letters placed in first move should come from the player's rack");
            }
        }
    }

    @Test
    void testConnectsToExistingTile() {
        List<Move> moves = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            assertTrue(legalMoveIterator.hasNext());
            moves.add(legalMoveIterator.next());
        }

        GameState newState = gameState.applyMove(moves.get(0), 0);
        PlayerView opponentView = PlayerView.fromGameState(newState, 1);

        LegalMoveIterator newIterator = new LegalMoveIterator(opponentView, new TrieDictionary(List.of("ANDD")));
        List<Move> opponentMoves = new ArrayList<>();

        assertTrue(newIterator.hasNext());
        opponentMoves.add(newIterator.next());
    }

    @Test
    void testStrings() {
        // Start a new empty game
        String boardString = String.valueOf(GameConstants.EMPTY_SQUARE).repeat(BoardConstants.TOTAL_SIZE);
        String bagToFirstMove = "XORANDIFELSE/STRINGS/AD/100/100";
        String gameString = boardString + "/" + bagToFirstMove;

        GameState gameState = GameState.stateFrom(gameString);
        PlayerView playerView = PlayerView.fromGameState(gameState, 0);

        // Create a simple dictionary containing a single word
        List<String> words = new ArrayList<>();
        words.add("SING");
        words.add("RING");
        words.add("RINGS");
        words.add("STRING");
        words.add("STRINGS");
        TrieDictionary dictionary = new TrieDictionary(words);

        LegalMoveIterator newIterator = new LegalMoveIterator(playerView, dictionary);
        MoveValidator moveValidator = new MoveValidator(dictionary);

        List<Move> moves = new ArrayList<>();
        while (newIterator.hasNext()) {
            Move move = newIterator.next();
            assertTrue(moveValidator.isValid(playerView.getBoard(), move),
                    "Invalid move: " + move);
            assertFalse(moves.contains(move));
            moves.add(move);
        }
    }

    @Test
    void testDoubleValidExtentionDuplicateDouble() {
        String boardString = "..............." +
                "..............." +
                "..............." +
                "..............." +
                "..............." +
                "..............." +
                "..............." +
                ".......A......." +
                "..............." +
                "..............." +
                "..............." +
                "..............." +
                "..............." +
                "..............." +
                "...............";
        boardString = boardString.replace('.', GameConstants.EMPTY_SQUARE);

        String bagToFirstMove = "XORANDIFELSE/P/AD/100/100";
        String gameString = boardString + "/" + bagToFirstMove;

        GameState gameState = GameState.stateFrom(gameString);
        PlayerView playerView = PlayerView.fromGameState(gameState, 0);

        // Create a simple dictionary containing a single word
        List<String> words = new ArrayList<>();
        words.add("P");
        TrieDictionary dictionary = new TrieDictionary(words);

        LegalMoveIterator newIterator = new LegalMoveIterator(playerView, dictionary);

        assertFalse(newIterator.hasNext());
    }

    @Test
    void testAdvancedPosition() {
        String boardString = "..............." +
                "..............." +
                "..............." +
                "..............." +
                "..............." +
                "..............." +
                ".......A......." +
                ".....SAND......" +
                ".....C.D......." +
                ".....R........." +
                ".....A........." +
                ".....B........." +
                ".....B........." +
                ".....L........." +
                ".....E.........";
        //////// 0123456789
        boardString = boardString.replace('.', GameConstants.EMPTY_SQUARE);

        String bagToFirstMove = "XORANDIFELSE/SQDSACN/AD/100/100";
        String gameString = boardString + "/" + bagToFirstMove;

        GameState gameState = GameState.stateFrom(gameString);
        PlayerView playerView = PlayerView.fromGameState(gameState, 0);

        // Create a simple dictionary containing a single word
        List<String> words = new ArrayList<>();
        words.add("AND");
        words.add("SAND");
        words.add("SCRABBLE"); // The 'SC' in this word helped me catch a but, thanks scrabble
        words.add("AQ");
        words.add("CQD");
        words.add("SA");
        words.add("QA");
        words.add("CDQ");
        words.add("AS");
        TrieDictionary dictionary = new TrieDictionary(words);

        LegalMoveIterator newIterator = new LegalMoveIterator(playerView, dictionary);
        MoveValidator moveValidator = new MoveValidator(dictionary);

        List<Move> moves = new ArrayList<>();
        while (newIterator.hasNext()) {
            Move move = newIterator.next();
            assertTrue(moveValidator.isValid(playerView.getBoard(), move),
                    "Invalid move: " + move);
            assertFalse(moves.contains(move));
            moves.add(move);
        }
        System.out.println("hej");
    }

    @Test
    void testKnownNumberOfLegalMoves() {
        String boardString = "..............." +
                "..............." +
                "..............." +
                "..............." +
                "..............." +
                "..............." +
                "..............." +
                "......WORDS...." +
                "..............." +
                "..............." +
                "..............." +
                "..............." +
                "..............." +
                "..............." +
                "...............";
        //////// 0123456789
        boardString = boardString.replace('.', GameConstants.EMPTY_SQUARE);

        String bagToFirstMove = "XORANDIFELSE/WORDS/AD/100/100";
        String gameString = boardString + "/" + bagToFirstMove;

        GameState gameState = GameState.stateFrom(gameString);
        PlayerView playerView = PlayerView.fromGameState(gameState, 0);

        // Create a simple dictionary containing a single word
        List<String> words = new ArrayList<>();
        words.add("WORD");
        words.add("WORDS");
        words.add("WW");
        TrieDictionary dictionary = new TrieDictionary(words);

        LegalMoveIterator newIterator = new LegalMoveIterator(playerView, dictionary);
        MoveValidator moveValidator = new MoveValidator(dictionary);

        List<Move> moves = new ArrayList<>();
        while (newIterator.hasNext()) {
            Move move = newIterator.next();
            assertTrue(moveValidator.isValid(playerView.getBoard(), move),
                    "Invalid move: " + move);
            assertFalse(moves.contains(move));
            moves.add(move);
        }
        assertEquals(11, moves.size());
    }

    @Test
    void testBuildCloseToEdges() {
        String boardString = "..............." +
                "..............." +
                "..............." +
                "..............." +
                "..............." +
                "..............." +
                ".CRABBLES......" +
                "......SCRABBLE." +
                "..............." +
                "..............." +
                "..............." +
                "..............." +
                "..............." +
                "..............." +
                "...............";
        //////// 0123456789
        boardString = boardString.replace('.', GameConstants.EMPTY_SQUARE);

        String bagToFirstMove = "XORANDIFELSE/S/AD/100/100";
        String gameString = boardString + "/" + bagToFirstMove;

        GameState gameState = GameState.stateFrom(gameString);
        PlayerView playerView = PlayerView.fromGameState(gameState, 0);

        // Create a simple dictionary containing a single word
        List<String> words = new ArrayList<>();
        words.add("SCRABBLES");
        TrieDictionary dictionary = new TrieDictionary(words);

        LegalMoveIterator newIterator = new LegalMoveIterator(playerView, dictionary);
        MoveValidator moveValidator = new MoveValidator(dictionary);

        List<Move> moves = new ArrayList<>();
        while (newIterator.hasNext()) {
            Move move = newIterator.next();
            assertTrue(moveValidator.isValid(playerView.getBoard(), move),
                    "Invalid move: " + move);
            assertFalse(moves.contains(move));
            moves.add(move);
        }
        assertEquals(2, moves.size());
    }

    @Test
    void testBuildCloseToCorner() {
        String boardString = ".XX.........XX." +
                "..............." +
                "..............." +
                "..............." +
                "..............." +
                "..............." +
                "..............." +
                "..............." +
                "..............." +
                "..............." +
                "..............." +
                "..............." +
                "..............X" +
                "..............X" +
                ".XX............";
        //////// 0123456789
        boardString = boardString.replace('.', GameConstants.EMPTY_SQUARE);

        String bagToFirstMove = "XORANDIFELSE/A/AD/100/100";
        String gameString = boardString + "/" + bagToFirstMove;

        GameState gameState = GameState.stateFrom(gameString);
        PlayerView playerView = PlayerView.fromGameState(gameState, 0);

        // Create a simple dictionary containing a single word
        List<String> words = new ArrayList<>();
        words.add("XX");
        words.add("XXA");
        words.add("AXX");
        TrieDictionary dictionary = new TrieDictionary(words);

        LegalMoveIterator newIterator = new LegalMoveIterator(playerView, dictionary);
        MoveValidator moveValidator = new MoveValidator(dictionary);

        List<Move> moves = new ArrayList<>();
        while (newIterator.hasNext()) {
            Move move = newIterator.next();
            assertTrue(moveValidator.isValid(playerView.getBoard(), move),
                    "Invalid move: " + move);
            assertFalse(moves.contains(move));
            moves.add(move);
        }
        assertEquals(8, moves.size());
    }
}