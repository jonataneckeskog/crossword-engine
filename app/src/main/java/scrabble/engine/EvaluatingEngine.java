package scrabble.engine;

import scrabble.core.Move;
import scrabble.core.components.Rack;
import scrabble.core.GameState;
import scrabble.core.PlayerView;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Iterator;
import java.util.HashMap;

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

        // Create an iterator of potential opponent Racks
        Iterator<Rack> rackIterator = RackSimulator.stream(playerView).iterator();

        // Generate a batch of 10 GameStates to be used for early evaluation (quick
        // eval)
        int initialBatchSize = 10;
        List<Rack> firstBatchStates = new ArrayList<>();
        for (int i = 0; i < initialBatchSize && rackIterator.hasNext(); i++) {
            firstBatchStates.add(rackIterator.next());
        }
        int processedStates = initialBatchSize;

        // Start processing the original batch, break immidiately if time runs out
        Map<Move, PlayerView> moveMap = new HashMap<>();
        MoveGenerator.streamLegalMoves(playerView)
                .takeWhile(move -> isActive && System.currentTimeMillis() - startTime < maxTimeMillis)
                .forEach(move -> {
                    PlayerView newPlayerView = playerView.applyMove(move);
                    moveMap.put(move, newPlayerView);

                    double totalEval = 0;
                    for (Rack rack : firstBatchStates) {
                        GameState newGameState = GameState.fromPlayerView(newPlayerView, rack);
                        totalEval += evaluator.evaluate(newGameState);
                    }

                    double averageEval = totalEval / firstBatchStates.size();
                    moveEvaluations.put(move, averageEval);

                    // Update listener after each move
                    if (listener != null) {
                        listener.update(new LinkedHashMap<>(moveEvaluations));
                    }
                });

        while (rackIterator.hasNext() && isActive && System.currentTimeMillis() - startTime < maxTimeMillis) {

            Rack rack = rackIterator.next();

            for (Move move : moveMap.keySet()) {
                GameState newState = GameState.fromPlayerView(moveMap.get(move), rack);
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
                for (Move move : moveMap.keySet()) {
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
