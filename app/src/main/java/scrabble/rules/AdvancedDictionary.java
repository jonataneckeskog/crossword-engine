package scrabble.rules;

public interface AdvancedDictionary extends Dictionary {
    boolean isPrefix(String prefix);

    boolean isSuffix(String suffix);
}