package scrabble.ui.view;

import java.awt.Color;

public enum BonusType {
    NONE("", Color.LIGHT_GRAY),
    DOUBLE_LETTER("DL", Color.CYAN),
    TRIPLE_LETTER("TL", Color.BLUE),
    DOUBLE_WORD("DW", Color.PINK),
    TRIPLE_WORD("TW", Color.RED),
    START("*", Color.ORANGE); // center star tile

    private final String label;
    private final Color color;

    BonusType(String label, Color color) {
        this.label = label;
        this.color = color;
    }

    public static BonusType fromValue(int value) {
        switch (value) {
            case 0:
                return NONE;
            case 1:
                return DOUBLE_LETTER;
            case 2:
                return TRIPLE_LETTER;
            case 3:
                return DOUBLE_WORD;
            case 4:
                return TRIPLE_WORD;
            default:
                return START;
        }
    }

    public String getLabel() {
        return label;
    }

    public Color getColor() {
        return color;
    }
}