package scrabble.core;

import org.junit.jupiter.api.Test;
import scrabble.core.Position.Step;
import scrabble.core.components.Board;
import scrabble.rules.game.BoardConstants;
import scrabble.rules.game.GameRules;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeAll;

class PositionTest {

    @BeforeAll
    static void setup() {
        GameRules.load("src/main/resources/scrabble.json"); // or correct path
    }

    @Test
    void testFromIndex() {
        int index = 12;
        Position pos = Position.fromIndex(index);
        assertEquals(index / BoardConstants.SIZE, pos.row());
        assertEquals(index % BoardConstants.SIZE, pos.column());
    }

    @Test
    void testStep() {
        Position pos = new Position(3, 3);
        Position right = pos.step(Step.RIGHT);
        assertEquals(3, right.row());
        assertEquals(4, right.column());

        Position down = pos.step(Step.DOWN);
        assertEquals(4, down.row());
        assertEquals(3, down.column());
    }

    @Test
    void testTryStepWithinBounds() {
        Board board = Board.emptyBoard();
        Position pos = new Position(3, 3);

        Position newPos = pos.tryStep(Step.RIGHT, board);
        assertNotNull(newPos);
        assertEquals(3, newPos.row());
        assertEquals(4, newPos.column());
    }

    @Test
    void testTryStepOutOfBounds() {
        Board board = Board.emptyBoard();
        Position pos = new Position(0, 0);

        Position newPos = pos.tryStep(Step.UP, board);
        assertNull(newPos);

        newPos = pos.tryStep(Step.LEFT, board);
        assertNull(newPos);
    }

    @Test
    void testToIndex() {
        Position pos = new Position(2, 5);
        int expectedIndex = 2 * BoardConstants.SIZE + 5;
        assertEquals(expectedIndex, pos.toIndex());
    }

    @Test
    void testStepUtilities() {
        assertEquals(Step.UP, Step.reverseStep(Step.DOWN));
        assertEquals(Step.LEFT, Step.reverseStep(Step.RIGHT));

        assertEquals(Step.RIGHT, Step.otherStep(Step.DOWN));
        assertEquals(Step.UP, Step.otherStep(Step.LEFT));
    }
}