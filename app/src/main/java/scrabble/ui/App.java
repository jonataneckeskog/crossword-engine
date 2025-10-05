package scrabble.ui;

import javax.swing.SwingUtilities;

import scrabble.engine.EvaluatingEngine;
import scrabble.ui.controller.GameController;
import scrabble.ui.model.EnginePlayer;
import scrabble.ui.model.HumanPlayer;
import scrabble.ui.model.Player;

public class App {
    public static void main(String[] args) {
        int timeControl = 30000;
        Player[] players = new Player[] { new HumanPlayer(timeControl, null),
                new EnginePlayer(new EvaluatingEngine(), timeControl, null) };

        // Just create the controller; it will handle model setup internally
        GameController controller = new GameController(timeControl);

        // Launch GUI on EDT
        SwingUtilities.invokeLater(controller::launchUI);
    }
}