package scrabble.ui.view;

import javax.swing.*;
import java.awt.*;

import scrabble.ui.model.Game;

public class GameWindow extends JFrame {
    private BoardUI board;
    private RackUI[] racks;
    private JLabel statusLabel;

    private GameWindow(BoardUI board, RackUI[] racks) {
        super("Scrabble Game");
        this.board = board;
        this.racks = racks;

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Add the board to the center
        add(board, BorderLayout.CENTER);

        // Add racks to the bottom
        JPanel rackPanel = new JPanel();
        rackPanel.setLayout(new GridLayout(1, racks.length));
        for (RackUI rack : racks) {
            rackPanel.add(rack);
        }
        add(rackPanel, BorderLayout.SOUTH);

        // Add status label at the top
        statusLabel = new JLabel("Welcome to Scrabble!");
        add(statusLabel, BorderLayout.NORTH);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    /** Shows a temporary tile at the given row, col, with the given letter */
    public void showTilePreview(int row, int col, char letter) {
        // Delegate to BoardUI to display a temporary tile
        board.showTemporaryTile(row, col, letter);
        board.repaint();
    }

    /** Clears all temporary tiles from the board */
    public void clearTilePreview() {
        board.clearTemporaryTiles();
        board.repaint();
    }

    /** Updates board, racks, and any other UI from the Game model */
    public void update(Game game) {
        board.update(game.getBoard());
        for (int i = 0; i < racks.length; i++) {
            racks[i].update(game.getPlayerRack(i));
        }
        repaint();
    }

    /** Factory method to create a GameWindow from a Game instance */
    public static GameWindow fromGame(Game game) {
        BoardUI boardUI = new BoardUI(game.getBoard());
        RackUI[] racksUI = new RackUI[2];
        for (int i = 0; i < racksUI.length; i++) {
            racksUI[i] = new RackUI(game.getPlayerRack(i));
        }
        return new GameWindow(boardUI, racksUI);
    }

    /** Shows an error message in a dialog */
    public void showError(String error) {
        JOptionPane.showMessageDialog(this, error, "Error", JOptionPane.ERROR_MESSAGE);
    }

    /** Shows a message in the status bar */
    public void showMessage(String message) {
        statusLabel.setText(message);
    }

    /** Optional: register undo click callback */
    public void onUndoClicked(Runnable callback) {
        // Example: add a menu or button for undo
        JMenuBar menuBar = getJMenuBar();
        if (menuBar == null) {
            menuBar = new JMenuBar();
            setJMenuBar(menuBar);
        }
        JMenu gameMenu = new JMenu("Game");
        JMenuItem undoItem = new JMenuItem("Undo");
        undoItem.addActionListener(e -> callback.run());
        gameMenu.add(undoItem);
        menuBar.add(gameMenu);
    }
}