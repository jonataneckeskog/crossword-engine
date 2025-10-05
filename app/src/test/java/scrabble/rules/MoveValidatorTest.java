package scrabble.rules;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import scrabble.core.*;
import scrabble.core.components.Board;
import scrabble.rules.game.GameRules;

public class MoveValidatorTest {
    private MoveValidator validator;

    @BeforeAll
    static void initialSetup() {
        GameRules.load("src/main/resources/scrabble.json"); // or correct path
    }

    @BeforeEach
    void setup() {
        List<String> words = List.of("EGG");
        TrieDictionary dictionary = new TrieDictionary(words);
        validator = new MoveValidator(dictionary);
    }

    @Test
    void testLegalMove() {
        Position[] postions = new Position[] { new Position(7, 7), new Position(8, 7), new Position(9, 7) };
        char[] letters = new char[] { 'E', 'G', 'G' };
        Move move = new Move(postions, letters);
        assertTrue(validator.isValid(Board.emptyBoard(), move));
    }
}
