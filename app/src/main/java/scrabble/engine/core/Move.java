package scrabble.engine.core;

import scrabble.engine.core.Position.Step;

import java.util.Map;
import java.util.Iterator;

public class Move {
    private Map<Position, Tile> tilePlacementMap;

    public Move() {

    }

    private Step stepFromMap() {
        if (tilePlacementMap.size() == 1)
            return Step.RIGHT;

        Iterator<Map.Entry<Position, Tile>> it = tilePlacementMap.entrySet().iterator();

        if (it.hasNext()) {
            Map.Entry<Position, Tile> entry1 = it.next();
            if (it.hasNext()) {
                Map.Entry<Position, Tile> entry2 = it.next();
                return entry1.getKey().row() == entry2.getKey().row() ? Step.RIGHT : Step.DOWN;
            }
        }

        return Step.RIGHT; // fallback
    }

    public Map<Position, Tile> getTilePlacemenetMap() {
        return tilePlacementMap;
    }

    public Step getStep() {
        return stepFromMap();
    }
}
