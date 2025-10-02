package scrabble.rules;

import scrabble.rules.game.*;

import java.util.Optional;

public final class TrieNode {
    public final TrieNode[] children = new TrieNode[BagConstants.UNIQUE_TILES];
    public boolean isWord;

    TrieNode(boolean isWord) {
        this.isWord = isWord;
    }

    public Optional<TrieNode> getChild(char letter) {
        return Optional.ofNullable(children[BagConstants.getIndex(Character.toUpperCase(letter))]);
    }

    public void setChild(char letter, TrieNode node) {
        children[BagConstants.getIndex(letter)] = node;
    }
}