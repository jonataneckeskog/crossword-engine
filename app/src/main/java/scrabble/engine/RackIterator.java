package scrabble.engine;

import scrabble.core.PlayerView;
import scrabble.core.components.Rack;
import scrabble.rules.game.BagConstants;
import scrabble.rules.game.GameConstants;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Random;

public class RackIterator implements Iterator<Rack> {
    private byte[] frequencyMap;
    private int bagSize;
    private int rackSize;
    private static final Random RANDOM = new Random();

    public RackIterator(PlayerView playerView) {
        this.frequencyMap = Arrays.copyOf(playerView.getBag().getFrequencyMap(),
                playerView.getBag().getFrequencyMap().length);
        this.bagSize = playerView.getBag().size();
        this.rackSize = Math.min(GameConstants.RACK_SIZE, playerView.getBag().size());
    }

    private static char drawTile(byte[] frequencyMap) {
        int totalTiles = 0;
        for (byte count : frequencyMap)
            totalTiles += count;

        if (totalTiles == 0)
            return '\0';

        int r = RANDOM.nextInt(totalTiles);
        for (int i = 0; i < frequencyMap.length; i++) {
            if (r < frequencyMap[i]) {
                frequencyMap[i]--;
                return BagConstants.INDEX_TO_CHAR[i];
            }
            r -= frequencyMap[i];
        }
        return '\0';
    }

    @Override
    public boolean hasNext() {
        int total = 0;
        for (byte count : frequencyMap)
            total += count;
        return total > 0;
    }

    @Override
    public Rack next() {
        if (!hasNext()) {
            throw new NoSuchElementException("No more tiles in the bag");
        }

        char[] rack = new char[Math.min(rackSize, bagSize)];
        int filled = 0;

        byte[] newFreqMap = frequencyMap.clone();
        for (int i = 0; i < rack.length; i++) {
            char tile = drawTile(newFreqMap);
            if (tile == '\0')
                break;
            rack[filled++] = tile;
        }

        // Trim if not fully filled
        if (filled < rack.length) {
            rack = Arrays.copyOf(rack, filled);
        }

        return Rack.fromString(new String(rack));
    }
}