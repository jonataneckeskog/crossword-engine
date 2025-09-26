package scrabble.engine.rules;

import java.util.HashMap;
import java.util.Map;

public final class Trie {
    private final TrieNode root;

    public Trie() {
        root = new TrieNode(false);
    }

    /** Insert a word into the trie */
    public void insert(String word) {
        TrieNode current = root;
        for (int i = 0, n = word.length(); i < n; i++) {
            char c = word.charAt(i);
            boolean isWord = (i == n - 1);

            // Reuse existing node if present
            current.children.putIfAbsent(c, new TrieNode(false));
            current = current.children.get(c);

            // Only mark the last node as a word
            if (isWord) {
                current.isWord = true;
            }
        }
    }

    /** Check if a word exists in the trie */
    public boolean containsWord(String word) {
        TrieNode current = root;
        for (int i = 0, n = word.length(); i < n; i++) {
            TrieNode next = current.children.get(word.charAt(i));
            if (next == null) {
                return false;
            }
            current = next;
        }
        return current.isWord;
    }

    public boolean containsPrefix(String prefix) {
        TrieNode current = root;
        for (int i = 0; i < prefix.length(); i++) {
            current = current.children.get(prefix.charAt(i));
            if (current == null)
                return false;
        }
        return true;
    }

    /** Inner node class */
    private static final class TrieNode {
        private final Map<Character, TrieNode> children = new HashMap<>();
        private boolean isWord;

        TrieNode(boolean isWord) {
            this.isWord = isWord;
        }
    }
}