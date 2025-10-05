package scrabble.ui.view;

import java.awt.Color;
import java.awt.Graphics;

public class BoardTile extends Tile {
    private final BonusType bonus;

    public BoardTile(BonusType bonus) {
        super(bonus.getColor()); // background comes from bonus
        this.bonus = bonus;
    }

    public BonusType getBonus() {
        return bonus;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // draw bonus text (like DL, TW, etc.)
        g.setColor(Color.BLACK);
        g.drawString(bonus.getLabel(), 10, 20);
    }
}