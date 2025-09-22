package scrabble.engine.core;

public sealed interface Tile permits LetterTile, BlankTile {
    char letter();

    int points();

    boolean isBlank();

    public static LetterTile createTile(char letter, int points) {
        return LetterTile.of(letter, points);
    }

    public static BlankTile createBlank() {
        return BlankTile.unassigned();
    }

    public static BlankTile createAssigned(char letter) {
        return BlankTile.assigned(letter);
    }
}