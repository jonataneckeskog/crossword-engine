package scrabble.engine.core;

import java.util.List;

public record DrawResult(Bag bag, List<Tile> drawnTiles) {
}