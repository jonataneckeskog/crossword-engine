package scrabble.engine.core;

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

    public Rack[] getRack() {
        return racks;
    }

    public int[] getScores() {
        return scores.clone();
    }

    public int getPlayerTurn() {
        return playerTurn;
    }
}
