package scrabble.ui.model;

public class TurnTimer {
    private final int playerCount;
    private final int timeControlMillis;
    private final int[] remainingTime;
    private int currentPlayer;
    private boolean running = false;

    private Thread timerThread;

    private Runnable onTimeExpired; // callback to controller

    public TurnTimer(int playerCount, int timeControlMillis) {
        this.playerCount = playerCount;
        this.timeControlMillis = timeControlMillis;
        this.remainingTime = new int[playerCount];
        for (int i = 0; i < playerCount; i++)
            remainingTime[i] = timeControlMillis;
    }

    public void setOnTimeExpired(Runnable callback) {
        this.onTimeExpired = callback;
    }

    public void startPlayer(int player) {
        stop(); // stop current timer
        currentPlayer = player;
        running = true;

        timerThread = new Thread(() -> {
            while (running && remainingTime[currentPlayer] > 0) {
                try {
                    Thread.sleep(100); // update every 100ms
                } catch (InterruptedException e) {
                    break;
                }
                remainingTime[currentPlayer] -= 100;
            }

            if (running && remainingTime[currentPlayer] <= 0 && onTimeExpired != null) {
                onTimeExpired.run();
            }
        });
        timerThread.start();
    }

    public void stop() {
        running = false;
        if (timerThread != null)
            timerThread.interrupt();
    }

    public int getRemainingTime(int player) {
        return remainingTime[player];
    }
}