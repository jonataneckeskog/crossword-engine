package scrabble.ui.model;

import scrabble.core.Move;
import scrabble.engine.Engine;

public class EnginePlayer implements Player {
    private Engine engine;
    private int playerId;
    private String name;

    public EnginePlayer(Engine engine, int playerId, String name) {
        this.engine = engine;
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
    public Move chooseMove(Game game, int timeMillis) {
        return engine.chooseMove(game.getPlayerView(playerId), timeMillis);
    }

    @Override
    public boolean isHuman() {
        return false;
    }
}
