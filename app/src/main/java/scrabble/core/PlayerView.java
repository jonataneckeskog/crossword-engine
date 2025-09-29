package scrabble.core;

import scrabble.core.components.Bag;
import scrabble.core.components.Board;
import scrabble.core.components.Rack;
import scrabble.rules.MoveScorer;

public final class PlayerView {
    private final Board board;
    private final Bag bag;
    private final Rack rack;
    private final int[] scores;
    private final int playerId;
    private final boolean isFirstMove;

    public PlayerView(Board board, Bag bag, Rack rack, int[] scores, int playerId, boolean isFirstMove) {
        this.board = board;
        this.bag = bag;
        this.rack = rack;
        this.scores = scores;
        this.playerId = playerId;
        this.isFirstMove = isFirstMove;
    }

    public PlayerView applyMove(Move move) {
        Board newBoard = board.placeWord(move);

        Rack newRack = rack.removeTiles(move.getTiles());

        int[] newScores = scores.clone();
        newScores[playerId] += MoveScorer.score(newBoard, move);

        return new PlayerView(newBoard, bag, newRack, newScores, playerId, false);
    }

    public static PlayerView fromGameState(GameState gameState, int playerId) {
        Board newBoard = gameState.getBoard();

        Rack[] oldRacks = gameState.getRacks();
        Bag newBag = gameState.getBag().addTiles(oldRacks[playerId == 0 ? 1 : 0].getLetters());
        Rack newRack = oldRacks[playerId];

        return new PlayerView(newBoard, newBag, newRack, gameState.getScores(), playerId, gameState.isFirstMove());
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
        return scores.clone();
    }

    public int getPlayerId() {
        return playerId;
    }

    public boolean isFirstMove() {
        return isFirstMove;
    }
}