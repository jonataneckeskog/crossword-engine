package scrabble.engine.core;

import org.junit.jupiter.api.Test;

import scrabble.engine.core.Position.Step;
import scrabble.engine.util.BoardConstants;

import static org.junit.jupiter.api.Assertions.*;

public class PositionTest {
    public static final int SIZE = BoardConstants.SIZE;

    @Test
    void testConstructor() {
        assertDoesNotThrow(() -> {
            new Position(0, 0);
        });
        assertDoesNotThrow(() -> {
            new Position(SIZE - 1, SIZE - 1);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            new Position(-1, 0);
        });
    }

    @Test
    void testStep() {
        Position position1 = new Position(2, 2);

        assertEquals(new Position(2, 3), position1.step(Step.RIGHT));
        assertEquals(new Position(2, 1), position1.step(Step.LEFT));
        assertEquals(new Position(1, 2), position1.step(Step.UP));
        assertEquals(new Position(3, 2), position1.step(Step.DOWN));

        assertThrows(IllegalArgumentException.class, () -> {
            position1.step(Step.UP).step(Step.UP).step(Step.UP); // Stepping outside the board
        });
    }
}
