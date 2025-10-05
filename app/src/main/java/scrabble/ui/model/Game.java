package scrabble.ui.model;

import scrabble.core.*;
import scrabble.rules.DictionaryProvider;
import scrabble.rules.MoveValidator;

import java.util.ArrayDeque;
import java.util.Deque;

public class Game {
    private GameState gameState;
    private PlayerView[] playerViews;
    private Player[] players;
    private int[] timeMillis;
    private int playerTurn;
    private int timeControl;

    private MoveValidator moveValidator = new MoveValidator(DictionaryProvider.get());

    /** History stack for undo */
    private final Deque<GameState> history = new ArrayDeque<>();
    private final Deque<Integer> turnHistory = new ArrayDeque<>();

    public Game(GameState gameState, Player player0, Player player1, int timeControl) {
        this.gameState = gameState;

        playerViews = new PlayerView[2];
        playerViews[0] = PlayerView.fromGameState(gameState, 0);
        playerViews[1] = PlayerView.fromGameState(gameState, 1);

        players = new Player[2];
        players[0] = player0;
        players[1] = player1;

        timeMillis = new int[2];
        timeMillis[0] = timeControl;
        timeMillis[1] = timeControl;

        playerTurn = 0;
        this.timeControl = timeControl;
    }

    public static Game newGame(Player player0, Player player1, int timeControl) {
        return new Game(GameState.startState(), player0, player1, timeControl);
    }

    public void reset(Player player0, Player player1) {
        this.gameState = GameState.startState();
        this.playerViews[0] = PlayerView.fromGameState(gameState, 0);
        this.playerViews[1] = PlayerView.fromGameState(gameState, 1);
        this.players[0] = player0;
        this.players[1] = player1;
        this.timeMillis[0] = timeControl;
        this.timeMillis[1] = timeControl;
        this.playerTurn = 0;
    }

    /**
     * Apply a move to the game.
     * Returns true if valid, false otherwise.
     */
    public boolean update(Move move) {
        if (!moveValidator.isValid(gameState.getBoard(), move))
            return false;

        // Save current state for undo
        history.push(gameState);
        turnHistory.push(playerTurn);

        // Apply move
        gameState = gameState.applyMove(move, playerTurn);

        // Update player views
        for (int i = 0; i < playerViews.length; i++) {
            playerViews[i] = PlayerView.fromGameState(gameState, i);
        }

        // Advance turn
        playerTurn = 1 - playerTurn;
        return true;
    }

    /** Undo the last move */
    public boolean undo() {
        if (history.isEmpty()) {
            return false; // nothing to undo
        }

        // Restore previous state and turn
        gameState = history.pop();
        playerTurn = turnHistory.pop();

        // Regenerate player views
        for (int i = 0; i < playerViews.length; i++) {
            playerViews[i] = PlayerView.fromGameState(gameState, i);
        }

        return true;
    }

    public boolean isGameOver() {
        return gameState.isGameOver();
    }

    // --- Getters ---

    public PlayerView getPlayerView(int playerId) {
        return playerViews[playerId];
    }

    public char[] getBoard() {
        return gameState.getBoard().getBoard();
    }

    public char[] getPlayerRack(int playerTurn) {
        return playerViews[playerTurn].getRack().getLetters();
    }

    public int getTimeLeft(int playerId) {
        return timeMillis[playerId];
    }

    public int getPlayerTurn() {
        return playerTurn;
    }

    public int[] getScores() {
        return gameState.getScores();
    }
}