package net.htlgkr.berghammert;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

public class Setting extends JPanel {
    private static final long serialVersionUID = 1L;
    private final Board board;
    private final int x0, y0, rowLen, colLen, border;
    private final Color HIGHLIGHT, NEXTMOVE;
    private final MouseHandler mouseHandler;
    private int gameMode;
    private boolean gameComplete;

    public Setting(Board b, int x, int y, int rowLen,
                   int colLen, int border) {
        board = b;
        x0 = x;
        y0 = y;
        this.rowLen = rowLen;
        this.colLen = colLen;
        HIGHLIGHT = Color.YELLOW;
        NEXTMOVE = Color.orange;
        this.border = border;
        this.gameComplete = false;

        mouseHandler = new MouseHandler();
        this.addMouseListener(mouseHandler);
    }

    @Override
    public void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        graphics.setFont(new Font("Serif", Font.BOLD, 24));
        for (int i = 0; i <= (Board.rowMax - Board.rowMin); i++) {
            graphics.setColor(Color.BLACK);

            {    //made a local scope.
                int labelx = 3 * x0 / 2 + i * rowLen, labely = y0 - 5;
                String colTitle = (char) ('a' + i) + "";
                graphics.drawString(colTitle, labelx, labely);
            }

            for (int j = 0; j <= (Board.colMax - Board.colMin); j++) {
                graphics.setColor(Color.BLACK);
                if (i == 0) {
                    int labelX = x0 - 20, labelY = 2 * y0 + j * colLen;
                    String rowTitle = (char) ('8' - j) + "";
                    graphics.drawString(rowTitle, labelX, labelY);
                }

                Cell thisCell = board.getCellAt(Board.rowMax - j, i + Board.colMin);
                int x = x0 + i * rowLen, y = y0 + j * colLen;

                graphics.drawRect(x, y, colLen, rowLen);

                if ((i + j) % 2 == 1) {
                    graphics.setColor(Color.LIGHT_GRAY);
                    graphics.fillRect(x + border, y + border, colLen - border,
                            rowLen - border);
                }
                else {
                    graphics.setColor(Color.WHITE);
                    graphics.fillRect(x + border, y + border, colLen - border,
                            rowLen - border);
                }

                if (thisCell.isSelected()) {
                    graphics.setColor(HIGHLIGHT);
                    graphics.fillRect(x, y, colLen, rowLen);
                } else if (thisCell.isNextMove()) {

                    graphics.setColor(NEXTMOVE);
                    graphics.fillRect(x, y, colLen, rowLen);
                }
                String pieceType = board.getPieceType(thisCell);
                Class<? extends Figure> pieceClass = board.getPieceClass(thisCell);
                if (pieceType != null) {
                    BufferedImage img = null;

                    try {
                        img = ImageIO.read(pieceClass.getResource(pieceType + ".png"));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    Image scaledImg = img.getScaledInstance(colLen,
                            rowLen, Image.SCALE_SMOOTH);
                    Icon pieceIcon = new ImageIcon(scaledImg);

                    pieceIcon.paintIcon(this, graphics, x, y);
                }
            }
        }
    }

    private void checkMate(String playerColour) {
        JFrame message = new JFrame("Schachmatt!");
        message.add(new JLabel(playerColour + ", du hast verloren"));
        message.setVisible(true);
        message.setSize(300, 300);
    }


    public void setGameMode(int mode) {

        this.gameMode = mode;
    }

    public void check(String playerColour) {
        JFrame message = new JFrame("Schach");
        message.add(new JLabel(playerColour + ", du bist Schach"));
        message.setVisible(true);
        message.setSize(300, 300);
    }

    private void clicked(int x, int y) {
        if (this.gameComplete)
            return;

        int col = (x - x0) / rowLen;
        int row = (y - y0) / colLen;

        boolean moveHappened = board.clicked(Board.rowMax - row,
                col + Board.colMin);
        this.repaint();
        if (moveHappened && gameMode == 1) {
            moveHappened = true;
        }

        if (board.isCheckMate(Board.White)) {
            this.checkMate(Board.White);
            this.gameComplete = true;
        } else if (moveHappened && board.isUnderCheck(Board.White))
            this.check(Board.White);
        if (board.isCheckMate(Board.Black)) {
            this.checkMate(Board.Black);
            this.gameComplete = true;
        } else if (moveHappened && board.isUnderCheck(Board.Black))
            this.check(Board.Black);
    }

    private class MouseHandler implements MouseListener, MouseMotionListener {
        @Override
        public void mouseDragged(MouseEvent e) {
        }

        @Override
        public void mouseMoved(MouseEvent e) {
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            clicked(e.getX(), e.getY());
        }

        @Override
        public void mousePressed(MouseEvent e) {
        }

        @Override
        public void mouseReleased(MouseEvent e) {
        }

        @Override
        public void mouseEntered(MouseEvent e) {
        }

        @Override
        public void mouseExited(MouseEvent e) {
        }
    }

}


