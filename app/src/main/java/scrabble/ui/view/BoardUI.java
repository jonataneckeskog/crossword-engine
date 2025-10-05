package scrabble.ui.view;

import javax.swing.*;
import java.awt.*;
import scrabble.rules.game.*;
import scrabble.ui.controller.TileFactory;

public class BoardUI extends JPanel {
    private Tile[] board; // permanent tiles
    private Tile[] tempTiles; // temporary preview tiles

    public BoardUI(char[] boardChars) {
        this.board = fromCharArray(boardChars);
        this.tempTiles = new Tile[BoardConstants.TOTAL_SIZE];
        setPreferredSize(new Dimension(500, 500)); // example size
    }

    /** Update permanent board state from model */
    public void update(char[] boardChars) {
        this.board = fromCharArray(boardChars);
        repaint();
    }

    /** Convert char array to Tile array */
    private Tile[] fromCharArray(char[] charArray) {
        Tile[] newBoard = new Tile[BoardConstants.TOTAL_SIZE];
        for (int i = 0; i < newBoard.length; i++) {
            if (charArray[i] == GameConstants.EMPTY_SQUARE) {
                newBoard[i] = TileFactory.getBoardTile(BoardConstants.SCRABBLE_BOARD[i]);
            } else {
                newBoard[i] = TileFactory.getLetterTile(charArray[i]);
            }
        }
        return newBoard;
    }

    /** Show a temporary tile (preview) at row,col */
    public void showTemporaryTile(int row, int col, char letter) {
        int index = row * BoardConstants.SIZE + col;
        tempTiles[index] = TileFactory.getLetterTile(letter);
        repaint();
    }

    /** Clear all temporary tiles */
    public void clearTemporaryTiles() {
        tempTiles = new Tile[BoardConstants.TOTAL_SIZE];
        repaint();
    }

    /** Draw permanent tiles and overlay temporary tiles */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (int row = 0; row < BoardConstants.SIZE; row++) {
            for (int col = 0; col < BoardConstants.SIZE; col++) {
                int index = row * BoardConstants.SIZE + col;

                // draw permanent tile
                Tile t = board[index];
                if (t != null)
                    t.paintComponent(g);

                // draw temporary tile if present
                Tile temp = tempTiles[index];
                if (temp != null && temp instanceof LetterTile) {
                    ((LetterTile) temp).drawPreview(g);
                }
            }
        }
    }
}