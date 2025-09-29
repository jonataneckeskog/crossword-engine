package scrabble.engine;

import scrabble.core.Move;
import scrabble.core.GameState;
import scrabble.core.PlayerView;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Iterator;

public class EvaluatingEngine implements Engine {
    private Evaluator evaluator;
    private boolean isActive = true;

    public EvaluatingEngine(Evaluator evaluator) {
        this.evaluator = evaluator;
    }

    private Move runSearch(PlayerView playerView, long maxTimeMillis, SearchListener listener) {
        long startTime = System.currentTimeMillis();

        // Map to track cumulative evaluations for each move
        Map<Move, Double> moveEvaluations = new LinkedHashMap<>();

        // Create an iterator of GameStates
        Iterator<GameState> gameStateIterator = GameStateSimulator.stream(playerView).iterator();

        // Generate a batch of 10 GameStates to be used for early evaluation (quick
        // eval)
        int initialBatchSize = 10;
        List<GameState> firstBatchStates = new ArrayList<>();
        for (int i = 0; i < initialBatchSize && gameStateIterator.hasNext(); i++) {
            firstBatchStates.add(gameStateIterator.next());
        }
        int processedStates = initialBatchSize;

        // Start processing the original batch, break immidiately if time runs out
        Set<Move> moveSet = new LinkedHashSet<>();
        MoveGenerator.streamLegalMoves(playerView)
                .takeWhile(move -> isActive && System.currentTimeMillis() - startTime < maxTimeMillis)
                .forEach(move -> {
                    moveSet.add(move);

                    double totalEval = 0;
                    for (GameState state : firstBatchStates) {
                        GameState newState = state.applyMove(move);
                        totalEval += evaluator.evaluate(newState);
                    }

                    double averageEval = totalEval / firstBatchStates.size();
                    moveEvaluations.put(move, averageEval);

                    // Update listener after each move
                    if (listener != null) {
                        listener.update(new LinkedHashMap<>(moveEvaluations));
                    }
                });

        // Cache all moves
        List<Move> allMoves = new ArrayList<>(moveSet);

        while (gameStateIterator.hasNext() && isActive && System.currentTimeMillis() - startTime < maxTimeMillis) {

            GameState state = gameStateIterator.next();

            for (Move move : allMoves) {
                GameState newState = state.applyMove(move);
                double eval = evaluator.evaluate(newState);

                // Update running average for this move
                double oldAvg = moveEvaluations.getOrDefault(move, 0.0);
                double newAvg = oldAvg + (eval - oldAvg) / processedStates;
                moveEvaluations.put(move, newAvg);
            }

            processedStates++;

            // Update listener with average evaluations
            if (listener != null) {
                Map<Move, Double> avgEvaluations = new LinkedHashMap<>();
                for (Move move : allMoves) {
                    avgEvaluations.put(move, moveEvaluations.get(move) / processedStates);
                }
                listener.update(avgEvaluations);
            }
        }

        return moveEvaluations.entrySet().stream().max(Map.Entry.comparingByValue())
                .orElseThrow(NoSuchElementException::new).getKey();
    }

    @Override
    public Move chooseMove(PlayerView playerView, long timeMillis) {
        return runSearch(playerView, timeMillis, null);
    }

    @Override
    public void search(PlayerView playerView, SearchListener listener) {
        runSearch(playerView, Long.MAX_VALUE, listener);
    }

    @Override
    public void stop() {
        isActive = false;
    }
}
