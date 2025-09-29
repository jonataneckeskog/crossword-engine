package scrabble.rules;

import java.util.List;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;

public final class DictionaryProvider {
    private static TrieDictionary dictionary;

    private DictionaryProvider() {
    }

    /**
     * Loads a dictionary from a file. Only needs to be called once at startup.
     */
    public static void loadFromFile(String filePath) throws IOException {
        List<String> words = Files.readAllLines(Paths.get(filePath));
        dictionary = new TrieDictionary(words);
    }

    /**
     * Returns the single shared dictionary instance.
     */
    public static TrieDictionary get() {
        if (dictionary == null) {
            throw new IllegalStateException("Dictionary not loaded. Call loadFromFile() first.");
        }
        return dictionary;
    }
}