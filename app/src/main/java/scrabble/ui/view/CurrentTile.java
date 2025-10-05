package scrabble.ui.view;

import java.awt.Color;
import java.awt.Graphics;

public class CurrentTile extends BoardTile {
    private final Color highlightColor;

    public CurrentTile(BonusType bonus, Color highlightColor) {
        super(bonus);
        this.highlightColor = highlightColor;
    }

    @Override
    protected void paintComponent(Graphics g) {
        // fill with the highlight color instead of the normal bonus color
        g.setColor(highlightColor);
        g.fillRect(0, 0, getWidth(), getHeight());

        // draw the bonus label on top
        g.setColor(Color.BLACK);
        g.drawString(getBonus().getLabel(), 10, 20);

        // optional: draw border
        g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
    }
}