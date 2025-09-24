package scrabble.engine.core;

import scrabble.engine.core.components.Bag_Copy;
import scrabble.engine.core.components.Board;
import scrabble.engine.core.components.DrawHandler;
import scrabble.engine.core.components.PlacementResult;
import scrabble.engine.core.components.Rack;

public final class PlayerView {
    private final Board board;
    private final Bag_Copy bag;
    private final Rack rack;
    private final int[] scores;
    private final int playerTurn;

    private PlayerView(Board board, Bag_Copy bag, Rack rack, int[] scores, int playerTurn) {
        this.board = board;
        this.bag = bag;
        this.rack = rack;
        this.scores = scores.clone();
        this.playerTurn = playerTurn;
    }

    public static PlayerView startState() {
        Board board = Board.emptyBoard();
        Bag_Copy startBag = Bag_Copy.standardBag();
        Rack rack = Rack.emptyRack();

        DrawHandler drawResult = rack.drawFrom(startBag);

        int[] scores = new int[] { 0, 0 };
        int playerTurn = 0;

        return new PlayerView(board, drawResult.bag(), drawResult.rack(), scores,
                playerTurn);
    }

    public static PlayerView stateFrom(String string) {
        // TO-DO
        return startState();
    }

    public PlayerView applyMove(Move move) {
        PlacementResult makeMove = board.placeWord(move);
        int[] newScores = scores.clone();
        newScores[playerTurn] += makeMove.score();

        DrawHandler makeDraw = rack.drawFrom(bag);

        int newPlayerTurn = playerTurn == 0 ? 1 : 0;

        return new PlayerView(makeMove.board(), makeDraw.bag(), makeDraw.rack(), newScores, newPlayerTurn);
    }

    // Getters
    public Board getBoard() {
        return board;
    }

    public Bag_Copy getBag() {
        return bag;
    }

    public Rack getRack() {
        return rack;
    }

    public int[] getScores() {
        return scores.clone();
    }

    public int getPlayerTurn() {
        return playerTurn;
    }
}