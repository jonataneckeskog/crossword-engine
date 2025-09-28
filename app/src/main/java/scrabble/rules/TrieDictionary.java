package scrabble.rules;

import java.util.List;

public class TrieDictionary {
    private final Trie forwardsTrie;

    public TrieDictionary(List<String> words) {
        forwardsTrie = new Trie();

        for (String word : words) {
            forwardsTrie.insert(word);
        }
    }

    public boolean isWord(String word) {
        return forwardsTrie.containsWord(word);
    }

    public boolean isPrefix(char[] prefix) {
        return forwardsTrie.containsPrefix(prefix);
    }

    public TrieNode getRoot() {
        return forwardsTrie.getRoot();
    }
}