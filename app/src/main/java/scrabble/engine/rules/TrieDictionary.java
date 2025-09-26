package scrabble.engine.rules;

import java.util.List;

public class TrieDictionary implements AdvancedDictionary {
    private final Trie forwardsTrie;
    private final Trie backwardsTrie;

    public TrieDictionary(List<String> words) {
        forwardsTrie = new Trie();
        backwardsTrie = new Trie();

        for (String word : words) {
            forwardsTrie.insert(word);
            backwardsTrie.insert(new StringBuilder(word).reverse().toString());
        }
    }

    @Override
    public boolean isWord(String word) {
        return forwardsTrie.containsWord(word);
    }

    @Override
    public boolean isPrefix(String prefix) {
        return forwardsTrie.containsPrefix(prefix);
    }

    @Override
    public boolean isSuffix(String suffix) {
        return backwardsTrie.containsPrefix(new StringBuilder(suffix).reverse().toString());
    }
}