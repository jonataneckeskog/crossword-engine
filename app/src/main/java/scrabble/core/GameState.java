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
    private final boolean isFirstMove;

    public GameState(Board board, Bag bag, Rack[] racks, int[] scores, boolean isFirstMove) {
        this.board = board;
        this.bag = bag;
        this.racks = racks;
        this.scores = scores.clone();
        this.isFirstMove = isFirstMove;
    }

    public static GameState startState() {
        Board board = Board.emptyBoard();
        Bag startBag = Bag.standardBag();
        Rack rack1 = Rack.emptyRack();
        Rack rack2 = Rack.emptyRack();

        DrawHandler drawResult1 = rack1.drawFrom(startBag);
        DrawHandler drawResult2 = rack2.drawFrom(drawResult1.bag());

        int[] scores = new int[] { 0, 0 };

        return new GameState(board, drawResult2.bag(), new Rack[] { drawResult1.rack(), drawResult2.rack() }, scores,
                true);
    }

    public static GameState stateFrom(String string) {
        // TO-DO
        return startState();
    }

    public static GameState fromPlayerView(PlayerView playerView, Rack otherRack) {
        Board board = playerView.getBoard();
        int playerId = playerView.getPlayerId();

        Bag newBag = playerView.getBag().removeTiles(otherRack.getLetters());
        Rack[] newRacks = new Rack[2];
        newRacks[playerId == 0 ? 0 : 1] = playerView.getRack();

        return new GameState(board, newBag, newRacks, playerView.getScores(), playerView.isFirstMove());
    }

    public GameState applyMove(Move move, boolean validate, int playerId) {
        if (validate && !MoveValidator.isValid(board, move))
            return this;

        Board newBoard = board.placeWord(move);

        int[] newScores = scores.clone();
        newScores[playerId] += MoveScorer.score(board, move);

        return new GameState(newBoard, bag, racks.clone(), newScores, false);
    }

    public GameState drawNewTiles(int playerId) {
        DrawHandler drawResult = racks[playerId].drawFrom(bag);
        Bag newBag = drawResult.bag();
        Rack[] newRacks = racks.clone();
        newRacks[playerId] = drawResult.rack();

        return new GameState(board, newBag, newRacks, scores.clone(), false);
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

    public boolean isFirstMove() {
        return isFirstMove;
    }
}
