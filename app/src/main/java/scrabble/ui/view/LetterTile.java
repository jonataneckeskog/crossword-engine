package scrabble.ui.view;

import java.awt.*;

public class LetterTile extends Tile {
    private final char letter;
    private final int points;

    public LetterTile(Color color, char letter, int points) {
        super(color);
        this.letter = letter;
        this.points = points;
    }

    public char getLetter() {
        return letter;
    }

    public int getPoints() {
        return points;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // permanent tile drawing
        g.setColor(Color.YELLOW);
        g.fillRect(0, 0, getWidth(), getHeight());

        g.setColor(Color.BLACK);
        g.drawString(String.valueOf(letter), 10, 20);
        g.drawString(String.valueOf(points), getWidth() - 15, getHeight() - 5);
    }

    /** Draws the tile semi-transparent for preview purposes */
    public void drawPreview(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f)); // 50% opacity
        paintComponent(g2);
        g2.dispose();
    }
}