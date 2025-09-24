package scrabble.engine.core.components;

import java.util.List;

public record DrawResult(Bag bag, List<Tile> drawnTiles) {
}