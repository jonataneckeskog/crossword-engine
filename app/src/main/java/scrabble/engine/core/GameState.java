package scrabble.engine.core;

public final class GameState {
    private final Board board;
    private final Bag bag;
    private final Rack rack;
    private final int[] scores;
    private final int playerTurn;

    public GameState(Board board, Bag bag, Rack rack, int[] scores, int playerTurn) {
        this.board = board;
        this.bag = bag;
        this.rack = rack;
        this.scores = scores.clone();
        this.playerTurn = playerTurn;
    }

    public GameState applyMove(Move move) {
        // Handle the Move on the board
        // Drawn new tiles to the rack
        // Update player turn
        return this;
    }

    // Getters
    public Board getBoard() {
        return board;
    }

    public Bag getBag() {
        return bag;
    }

    public Rack getRack() {
        return rack;
    }

    public int[] getScores() {
        return scores;
    }

    public int getPlayerTurn() {
        return playerTurn;
    }
}
