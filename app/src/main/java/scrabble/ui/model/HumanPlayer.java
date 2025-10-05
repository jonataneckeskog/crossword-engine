package scrabble.ui.model;

public class HumanPlayer implements Player {
    private int playerId;
    private String name;

    public HumanPlayer(int playerId, String name) {
        this.playerId = playerId;
        this.name = name;
    }

    @Override
    public int getId() {
        return playerId;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isHuman() {
        return true;
    }
}
