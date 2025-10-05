package scrabble.ui.view;

import javax.swing.*;
import java.awt.*;

public class RackUI extends JPanel {
    private char[] rack;

    public RackUI(char[] tiles) {
        rack = tiles;
    }

    public void update(char[] tiles) {
        rack = tiles;
        repaint();
    }

    public char[] getRack() {
        return rack;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
    }
}