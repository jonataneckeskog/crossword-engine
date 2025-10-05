package scrabble.ui.view;

import javax.swing.*;
import java.awt.*;

public abstract class Tile extends JPanel {
    private Color color;

    public Tile(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(color);
        g.fillRect(0, 0, getWidth(), getHeight());
    }
}
