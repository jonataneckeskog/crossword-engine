package scrabble.rules;

public final class Trie {
    private final TrieNode root;

    public Trie() {
        root = new TrieNode(false);
    }

    // Insert a word into the trie
    public void insert(String word) {
        TrieNode current = root;
        for (int i = 0, n = word.length(); i < n; i++) {
            char c = word.charAt(i);
            boolean isWord = (i == n - 1);

            // Unwrap the Optional
            TrieNode next = current.getChild(c).orElse(null);

            if (next == null) {
                next = new TrieNode(false);
                current.setChild(c, next);
            }

            current = next;

            // Only mark the last node as a word
            if (isWord) {
                current.isWord = true;
            }
        }
    }

    // Check if a word exists in the trie
    public boolean containsWord(String word) {
        TrieNode current = root;
        for (int i = 0, n = word.length(); i < n; i++) {
            current = current.getChild(word.charAt(i)).orElse(null);
            if (current == null) {
                return false;
            }
        }
        return current.isWord;
    }

    // Check if a prefix exists in the trie
    public boolean containsPrefix(char[] prefix) {
        TrieNode current = root;
        for (char c : prefix) {
            current = current.getChild(c).orElse(null);
            if (current == null) {
                return false;
            }
        }
        return true;
    }

    public TrieNode getRoot() {
        return root;
    }
}