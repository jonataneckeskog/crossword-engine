package scrabble.engine.core.components;

public sealed interface Tile permits LetterTile, BlankTile {
    char letter();

    int points();

    boolean isBlank();

    public static LetterTile createLetter(char letter, int points) {
        return LetterTile.of(letter, points);
    }

    public static BlankTile createBlank(char letter) {
        return BlankTile.of(letter);
    }
}