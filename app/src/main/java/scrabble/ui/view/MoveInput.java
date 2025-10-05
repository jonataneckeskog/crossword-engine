package scrabble.ui.view;

import scrabble.core.Move;
import scrabble.core.Position;
import java.util.function.Consumer;

import javax.swing.JOptionPane;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles human move input: clicking squares and typing letters.
 * Once a move is ready, it calls back to the controller via moveCallback.
 */
public class MoveInput {
    private final GameWindow view;
    private final Consumer<Move> moveCallback;
    private final List<TilePlacement> currentMove;

    private boolean enabled = false;

    public MoveInput(GameWindow view, Consumer<Move> moveCallback) {
        this.view = view;
        this.moveCallback = moveCallback;
        this.currentMove = new ArrayList<>();
    }

    /** Allow user to start making a move */
    public void enable() {
        if (enabled)
            return;
        enabled = true;

        // Hook into board click events
        view.getBoardUI().onTileClicked((row, col) -> {
            if (!enabled)
                return;
            char letter = promptLetterInput();
            if (letter != 0) {
                placeTile(row, col, letter);
            }
        });

        // Hook into rack interactions if needed (e.g., selecting tiles)
        // view.getRackUI(0).onTileSelected(...);
    }

    /** Disable interaction */
    public void disable() {
        enabled = false;
        view.getBoardUI().clearTileClickListeners();
    }

    private char promptLetterInput() {
        // quick and dirty letter input prompt (can be improved)
        String s = JOptionPane.showInputDialog(view, "Enter letter:");
        if (s == null || s.isEmpty())
            return 0;
        return s.toUpperCase().charAt(0);
    }

    public void reset() {
        currentMove.clear();
        view.clearTilePreview();
    }

    public void placeTile(int row, int col, char letter) {
        currentMove.add(new TilePlacement(row, col, letter));
        view.showTilePreview(row, col, letter); // calls BoardUI.showTemporaryTile()
    }

    /** Called when the player submits the move */
    public void submitMove() {
        if (currentMove.isEmpty()) {
            return; // nothing to submit
        }

        Move move = buildMove();
        moveCallback.accept(move); // notify controller
        currentMove.clear();
    }

    /** Cancel the current move */
    public void cancelMove() {
        currentMove.clear();
        view.clearTilePreview(); // removes all temp tiles
    }

    /** Build a Move object from current placements */
    private Move buildMove() {
        Position[] positions = new Position[currentMove.size()];
        char[] letters = new char[currentMove.size()];
        for (int i = 0; i < positions.length; i++) {
            TilePlacement tp = currentMove.get(i);
            positions[i] = new Position(tp.row, tp.col);
            letters[i] = tp.letter;
        }
        return new Move(positions, letters);
    }

    /** Small helper class to store in-progress tile placements */
    private static class TilePlacement {
        int row, col;
        char letter;

        TilePlacement(int row, int col, char letter) {
            this.row = row;
            this.col = col;
            this.letter = letter;
        }
    }
}