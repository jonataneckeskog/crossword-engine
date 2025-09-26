package scrabble.core;

import scrabble.core.components.Bag;
import scrabble.core.components.Board;
import scrabble.core.components.DrawHandler;
import scrabble.core.components.Rack;

public final class PlayerView {
    private final Board board;
    private final Bag bag;
    private final Rack rack;
    private final int[] scores;

    public PlayerView(Board board, Bag bag, Rack rack, int[] scores) {
        this.board = board;
        this.bag = bag;
        this.rack = rack;
        this.scores = scores;
    }

    public PlayerView applyMove(Move move, char[] specifiedDraw, int score, int playerTurn) {
        Board newBoard = board.placeWord(move);

        Rack tempRack = rack;
        Bag tempBag = bag;

        if (specifiedDraw != null && specifiedDraw.length > 0) {
            tempBag = tempBag.removeTiles(specifiedDraw);
            tempRack = tempRack.addTiles(specifiedDraw);
        }

        DrawHandler drawResult = tempRack.drawFrom(tempBag);
        Bag newBag = drawResult.bag();
        Rack newRack = drawResult.rack();

        int[] newScores = scores.clone();
        newScores[playerTurn] += score;

        return new PlayerView(newBoard, newBag, newRack, newScores);
    }

    public static PlayerView fromGameState(GameState gameState, int playerId) {
        // TO-DO
        return null;
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
}