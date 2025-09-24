package scrabble.engine.core.components;

import org.junit.jupiter.api.Test;

import scrabble.engine.core.components.Position.Step;
import scrabble.engine.util.game.BoardConstants;

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

        assertEquals(new Position(2, 3), position1.tryStep(Step.RIGHT));
        assertEquals(new Position(2, 1), position1.tryStep(Step.LEFT));
        assertEquals(new Position(1, 2), position1.tryStep(Step.UP));
        assertEquals(new Position(3, 2), position1.tryStep(Step.DOWN));

        assertNull(position1.tryStep(Step.UP).tryStep(Step.UP).tryStep(Step.UP)); // Stepping outside the board
    }
}
