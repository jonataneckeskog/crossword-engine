package scrabble.engine;

import scrabble.core.PlayerView;
import scrabble.core.components.Bag;
import scrabble.core.components.Rack;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class RackIterator implements Iterator<Rack> {
    private PlayerView playerView;
    private Rack nextRack = null;

    public RackIterator(PlayerView playerView) {
        this.playerView = playerView;
        advance();
    }

    private void advance() {
        Bag newBag = playerView.getBag();
        Rack[] newRacks = new Rack[2];
    }

    @Override
    public boolean hasNext() {
        return nextRack != null;
    }

    @Override
    public Rack next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        Rack current = nextRack;
        advance();
        return current;
    }
}
