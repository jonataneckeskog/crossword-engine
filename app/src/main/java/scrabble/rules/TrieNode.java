package scrabble.rules;

import scrabble.rules.game.*;

public final class TrieNode {
    public final TrieNode[] children = new TrieNode[26];
    public boolean isWord;

    TrieNode(boolean isWord) {
        this.isWord = isWord;
    }

    public TrieNode getChild(char letter) {
        return children[BagConstants.getIndex(letter)];
    }

    public void setChild(char letter, TrieNode node) {
        children[BagConstants.getIndex(letter)] = node;
    }
}