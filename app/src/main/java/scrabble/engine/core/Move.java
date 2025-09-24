package scrabble.engine.core;

import java.util.Map;

import scrabble.engine.core.components.Position;
import scrabble.engine.core.components.Tile;
import scrabble.engine.core.components.Position.Step;

import java.util.Iterator;

public class Move {
    private Map<Position, Tile> tilePlacementMap;
    private Step step;

    public Move(Map<Position, Tile> tilePlacementMap) {
        this.tilePlacementMap = tilePlacementMap;
        this.step = stepFromMap();
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
        return step;
    }
}
