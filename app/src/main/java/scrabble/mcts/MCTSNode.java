package scrabble.mcts;

import scrabble.core.GameState;
import scrabble.core.Move;

import java.util.List;
import java.util.ArrayList;

public class MCTSNode {
    private GameState gameState;
    private Move move;
    private MCTSNode parent;
    private List<MCTSNode> children;
    private int visits;
    private double totalScore;
    private boolean expanded = false;

    public MCTSNode(GameState gameState, Move move, MCTSNode parent, double evaluationScore) {
        this.gameState = gameState;
        this.move = move;
        this.parent = parent;
        children = new ArrayList<>();
        visits = 0;
        totalScore = evaluationScore;
    }

    // Helpers
    public boolean isLeaf() {
        return children.isEmpty();
    }

    public double getAverageScore() {
        return visits == 0 ? 0 : totalScore / visits;
    }

    // Mutators
    public void addChild(MCTSNode child) {
        children.add(child);
    }

    public void incrementVisits() {
        visits++;
    }

    public void addScore(double score) {
        totalScore += score;
    }

    public void setExpanded(boolean val) {
        expanded = val;
    }

    // Accessors
    public GameState getGameState() {
        return gameState;
    }

    public Move getMove() {
        return move;
    }

    public MCTSNode getParent() {
        return parent;
    }

    public List<MCTSNode> getChildren() {
        return children;
    }

    public int getVisits() {
        return visits;
    }

    public double getTotalScore() {
        return totalScore;
    }

    public boolean isExpanded() {
        return expanded;
    }
}
