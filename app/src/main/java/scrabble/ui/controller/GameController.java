package scrabble.ui.controller;

import java.util.ArrayDeque;
import java.util.Deque;

import scrabble.core.Move;
import scrabble.ui.model.Game;
import scrabble.ui.model.Player;
import scrabble.ui.view.GameWindow;
import scrabble.ui.view.MoveInput;

/**
 * GameController orchestrates the game loop, manages history,
 * and coordinates between the model (Game) and the view (GameWindow).
 */
public class GameController {

    private final Game game;
    private final Player[] players;
    private final GameWindow gameWindow;
    private final MoveInput moveInput;
    private final Deque<Move> moveHistory = new ArrayDeque<>();
    private final int timeControl;

    /**
     * Create a new GameController with the given players and time control.
     * 
     * @param players     array of players (HumanPlayer, EnginePlayer, etc.)
     * @param timeControl time in milliseconds per turn
     */
    public GameController(Player[] players, int timeControl) {
        if (players == null || players.length < 2)
            throw new IllegalArgumentException("At least 2 players required");

        this.players = players;
        this.game = Game.newGame(players[0], players[1], timeControl);

        // Initialize the view and input handler
        this.gameWindow = GameWindow.fromGame(game);
        this.moveInput = new MoveInput(gameWindow, this::applyMove);

        this.timeControl = timeControl;

        registerViewEvents();
        updateView();
    }

    /** Launches the GUI on the Event Dispatch Thread */
    public void launchUI() {
        javax.swing.SwingUtilities.invokeLater(() -> {
            gameWindow.setVisible(true);
            promptCurrentPlayer();
        });
    }

    /** Starts a new game and clears history */
    public void startNewGame() {
        game.reset(players[0], players[1]);
        moveHistory.clear();
        updateView();
        promptCurrentPlayer();
    }

    /** Applies a move; returns true if valid, false otherwise */
    public boolean applyMove(Move move) {
        if (!game.update(move)) {
            gameWindow.showError("Invalid move!");
            return false;
        }

        moveHistory.push(move);
        updateView();
        checkGameOverOrNextTurn();
        return true;
    }

    /** Undo the last move */
    public void undoLastMove() {
        if (moveHistory.isEmpty()) {
            gameWindow.showError("Nothing to undo.");
            return;
        }

        moveHistory.pop(); // remove the last move from history
        if (game.undo()) { // restores previous state internally
            updateView();
            promptCurrentPlayer();
        } else {
            gameWindow.showError("Unable to undo the move.");
        }
    }

    /** Prompts the current player to make a move */
    private void promptCurrentPlayer() {
        if (game.isGameOver()) {
            gameWindow.showMessage("Game over! Scores: " + formatScores());
            return;
        }

        Player current = players[game.getPlayerTurn()];

        if (current.isHuman()) {
            moveInput.enable(); // wait for user input
            gameWindow.showMessage(current.getName() + "'s turn");
        } else {
            Move move = current.chooseMove(game, game.getTimeLeft(game.getPlayerTurn()));
            applyMove(move);
        }
    }

    /** Update the view with current game state */
    private void updateView() {
        gameWindow.update(game);
        moveInput.reset();
    }

    /** Checks if game is over, otherwise advances to next turn */
    private void checkGameOverOrNextTurn() {
        if (game.isGameOver()) {
            gameWindow.showMessage("Game over! Scores: " + formatScores());
        } else {
            promptCurrentPlayer();
        }
    }

    /** Register view events like undo button click */
    private void registerViewEvents() {
        gameWindow.onUndoClicked(this::undoLastMove);
    }

    /** Format scores for display */
    private String formatScores() {
        int[] scores = game.getScores();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < scores.length; i++) {
            sb.append(players[i].getName()).append(": ").append(scores[i]).append("  ");
        }
        return sb.toString();
    }
}