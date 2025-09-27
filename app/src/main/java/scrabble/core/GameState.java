package scrabble.core;

import scrabble.core.components.Bag;
import scrabble.core.components.Board;
import scrabble.core.components.DrawHandler;
import scrabble.core.components.Rack;

import scrabble.rules.MoveScorer;
import scrabble.rules.MoveValidator;

public final class GameState {
    private final Board board;
    private final Bag bag;
    private final Rack[] racks;
    private final int[] scores;
    private final int playerTurn;

    private GameState(Board board, Bag bag, Rack[] racks, int[] scores, int playerTurn) {
        this.board = board;
        this.bag = bag;
        this.racks = racks;
        this.scores = scores.clone();
        this.playerTurn = playerTurn;
    }

    public static GameState startState() {
        Board board = Board.emptyBoard();
        Bag startBag = Bag.standardBag();
        Rack rack1 = Rack.emptyRack();
        Rack rack2 = Rack.emptyRack();

        DrawHandler drawResult1 = rack1.drawFrom(startBag);
        DrawHandler drawResult2 = rack2.drawFrom(drawResult1.bag());

        int[] scores = new int[] { 0, 0 };
        int playerTurn = 0;

        return new GameState(board, drawResult2.bag(), new Rack[] { drawResult1.rack(), drawResult2.rack() }, scores,
                playerTurn);
    }

    public static GameState stateFrom(String string) {
        // TO-DO
        return startState();
    }

    public GameState applyMove(Move move) {
        if (!MoveValidator.isValid(board, move))
            return this;

        int[] newScores = scores.clone();
        newScores[playerTurn] += MoveScorer.score(board, move);

        Board newBoard = board.placeWord(move);

        DrawHandler makeDraw = racks[playerTurn].drawFrom(bag);
        Bag newBag = makeDraw.bag();
        Rack[] newRacks = racks.clone();
        newRacks[playerTurn] = makeDraw.rack();

        int newPlayerTurn = playerTurn == 0 ? 1 : 0;

        return new GameState(newBoard, newBag, newRacks, newScores, newPlayerTurn);
    }

    public boolean isGameOver() {
        return bag.isEmpty() && (racks[0].isEmpty() || racks[1].isEmpty());
    }

    // Getters
    public Board getBoard() {
        return board;
    }

    public Bag getBag() {
        return bag;
    }

    public Rack[] getRacks() {
        return racks.clone();
    }

    public int[] getScores() {
        return scores.clone();
    }

    public int getPlayerTurn() {
        return playerTurn;
    }
}
