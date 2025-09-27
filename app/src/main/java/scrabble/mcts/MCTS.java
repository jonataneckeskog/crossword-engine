package scrabble.mcts;

import scrabble.core.GameState;
import scrabble.core.Move;
import scrabble.engine.Evaluator;
import scrabble.engine.MoveGenerator;
import scrabble.rules.AdvancedDictionary;

import java.util.List;
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.AbstractMap.SimpleEntry;
import java.util.stream.Collectors;

public class MCTS {
    private MCTSNode root;
    private Evaluator evaluator;
    private AdvancedDictionary dictionary;
    private List<Move> legalMovesFromRoot;

    public MCTS(GameState gameState, Evaluator evaluator, AdvancedDictionary dictionary,
            List<Move> legalMovesFromRoot) {
        this.root = new MCTSNode(gameState, null, null, 0);
        this.evaluator = evaluator;
        this.dictionary = dictionary;
        this.legalMovesFromRoot = legalMovesFromRoot;
    }

    /** Original getBestMove logic, still available */
    public Move getBestMove(int maxExpansions, double explorationParam) {
        List<SimpleEntry<Move, Double>> moveValues = getRootMoveValues(maxExpansions, explorationParam);
        return moveValues.get(0).getKey(); // top move
    }

    /** Returns all root moves with their average scores */
    public List<SimpleEntry<Move, Double>> getRootMoveValues(int maxExpansions, double explorationParam) {
        int expansionsUsed = 0;

        PriorityQueue<MCTSNode> fringe = new PriorityQueue<>(
                (a, b) -> Double.compare(getUCT(b, explorationParam), getUCT(a, explorationParam)));
        fringe.add(root);

        while (!fringe.isEmpty() && expansionsUsed < maxExpansions) {
            MCTSNode node = fringe.poll();

            if (node.getGameState().isGameOver()) {
                double result = simulate(node);
                backpropagate(node, result);
                continue;
            }

            if (!node.isExpanded()) {
                expansionsUsed += expandNode(node, fringe, expansionsUsed, maxExpansions);
            }

            double result = simulate(node);
            backpropagate(node, result);
        }

        // Return all root children with their average scores, sorted descending
        return root.getChildren().stream()
                .map(child -> new SimpleEntry<>(child.getMove(), child.getAverageScore()))
                .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                .collect(Collectors.toList());
    }

    /** Expand a node and add children to the fringe */
    private int expandNode(MCTSNode node, PriorityQueue<MCTSNode> fringe, int expansionsUsed, int maxExpansions) {
        List<Move> moves = (node == root) ? legalMovesFromRoot
                : MoveGenerator.legalMoves(node.getGameState(), dictionary);

        if (moves != null && !moves.isEmpty()) {
            List<MCTSNode> children = new ArrayList<>();
            for (Move move : moves) {
                GameState newState = node.getGameState().applyMove(move);
                double parentEvaluation = node.getTotalScore();
                double newEvaluation = evaluator.evaluate(newState) - parentEvaluation;
                MCTSNode child = new MCTSNode(newState, move, node, newEvaluation);
                children.add(child);
            }

            // Sort children by total score (optional prioritization)
            children.sort((a, b) -> Double.compare(b.getTotalScore(), a.getTotalScore()));

            for (MCTSNode child : children) {
                if (expansionsUsed >= maxExpansions)
                    break;
                node.addChild(child);
                fringe.add(child);
                expansionsUsed++;
            }
        }

        node.setExpanded(true);
        return expansionsUsed;
    }

    /** Standard UCT calculation */
    private double getUCT(MCTSNode node, double explorationParam) {
        if (node.getVisits() == 0)
            return Double.MAX_VALUE;
        int parentVisits = node.getParent() != null ? node.getParent().getVisits() : 1;
        return node.getAverageScore() + explorationParam * Math.sqrt(Math.log(parentVisits) / node.getVisits());
    }

    /** Quick evaluation-based simulation */
    private double simulate(MCTSNode node) {
        return node.getTotalScore();
    }

    /** Backpropagate result up the tree */
    private void backpropagate(MCTSNode node, double result) {
        while (node != null) {
            node.incrementVisits();
            node.addScore(result);
            node = node.getParent();
        }
    }

    public MCTSNode getRoot() {
        return root;
    }
}