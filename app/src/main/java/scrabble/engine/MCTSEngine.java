package scrabble.engine;

import scrabble.rules.AdvancedDictionary;

import scrabble.core.Move;
import scrabble.core.PlayerView;

public class MCTSEngine implements Engine {

    public MCTSEngine(AdvancedDictionary dictionary) {

    }

    @Override
    public Move chooseMove(PlayerView playerView, long timeMillis) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'chooseMove'");
    }

    @Override
    public void search(PlayerView playerView, SearchListener listener) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'search'");
    }

    @Override
    public void stop() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'stop'");
    }

    @Override
    public Move getBestMoveSoFar() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getBestMoveSoFar'");
    }

}
