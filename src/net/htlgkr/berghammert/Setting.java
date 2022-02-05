package net.htlgkr.berghammert;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Setting extends JPanel
{
    private static final long serialVersionUID = 1L;
    private final Board board;
    private final int x0, y0, rowLen, colLen, border;
    private final Color HIGHLIGHT, NEXTMOVE;
    private final MouseHandler mouseHandler;
    private int gameMode;
    private boolean gameComplete;

    /**
     * Sets starting point of graphics as (x, y).
     * (Window before it will remain blank.)
     * Sets row-length and column length and border width.
     * Sets board.
     * Sets colour of a cell for highlight and next move.
     * Adds a mouse event handler for the graphics.
     * */
    public Setting(	Board b, int x, int y, int rowLen, int colLen, int border)
    {
        board = b;
        x0= x;
        y0= y;
        this.rowLen = rowLen;
        this.colLen = colLen;
        HIGHLIGHT = Color.YELLOW;
        NEXTMOVE = Color.orange;
        this.border = border;
        this.gameComplete = false;

        mouseHandler = new MouseHandler();
        this.addMouseListener(mouseHandler);
    }

    /**
     * Draws the board and shows pieces, row and column number/letters.
     * Marks highlighted cell with HIGHLIGHT colour and next move cell
     * with NEXTMOVE colour.
     * Paints alternate cell WHITE and GRAY colour.
     * */
    @Override
    public void paintComponent(Graphics graphics)
    {
        super.paintComponent(graphics);
        graphics.setFont(new Font(Font.SERIF, Font.BOLD, 24));
        for(int i = 0; i <= (Board.rowMax-Board.rowMin); i++)
        {
            graphics.setColor(Color.BLACK);

            {	//made a local scope.
                int labelx = 3*x0/2 + i*rowLen, labely = y0-5;
                String colTitle = (char)('a'+i) + "";
                graphics.drawString(colTitle, labelx, labely);
            }

            for(int j=0; j<=(Board.colMax-Board.colMin); j++)
            {
                graphics.setColor(Color.BLACK);
                if(i==0)
                {
                    int labelX = x0-20, labelY = 2*y0 + j*colLen;
                    String rowTitle = (char)('8'-j) + "";
                    graphics.drawString(rowTitle, labelX, labelY);
                }

                Cell thisCell = board.getCellAt(Board.rowMax-j, i+Board.colMin);
                int x= x0+ i*rowLen, y= y0+ j*colLen;

                graphics.drawRect(x, y, colLen, rowLen);
                //Draw the border of the cell.

                //paint the cell LIGHT_GRAY if it's even numbered.
                if((i+j) % 2 ==1)
                {	graphics.setColor(Color.LIGHT_GRAY);
                    graphics.fillRect(x+border, y+border, colLen-border, rowLen-border);
                }
                //otherwise paint it white.
                else
                {	graphics.setColor(Color.WHITE);
                    graphics.fillRect(x+border, y+border, colLen-border, rowLen-border);
                }

                if(thisCell.isSelected())
                {
                    //If the cell is selected, paint it
                    //with HIGHLIGHT colour.
                    graphics.setColor(HIGHLIGHT);
                    graphics.fillRect(x, y, colLen, rowLen);
                }
                else if(thisCell.isNextMove())
                {
                    //If the cell is selected, paint it
                    //with NEXTMOVE colour.
                    graphics.setColor(NEXTMOVE);
                    graphics.fillRect(x, y, colLen, rowLen);
                }
                String pieceType = board.getPieceType(thisCell);
                Class<? extends Figure> pieceClass = board.getPieceClass(thisCell);
                //draw the image of the piece contained by this cell.
                if(pieceType != null)
                {
                    BufferedImage img = null;

                    try
                    {
                        //System.out.println(pieceType);
                        img = ImageIO.read(pieceClass.getResource(pieceType+".png"));
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                        System.out.println("Exception in paintComponents()"
                                + " of GraphicsHandler.");
                    }

                    Image scaledImg = img.getScaledInstance(colLen, rowLen, Image.SCALE_SMOOTH);
                    //Scale the icon to the size of this cell.
                    Icon pieceIcon = new ImageIcon(scaledImg);

                    pieceIcon.paintIcon(this, graphics, x, y);
                }
            }
        }
    }


    private void checkMate(String playerColour)
    {
        JFrame message = new JFrame("Sorry !!");
        message.add(new JLabel(playerColour+", you lost the game :'("));
        message.setVisible(true);
        message.setSize(300, 300);
    }


    /**
     * Finds the board cell at (x,y) coordinates of graphics.
     * Calls board.clicked() with the row and column of the clicked cell.
     * Redraws all the graphics of the window.
     * Checks whether the game has ended, and shows a message if that is true.
     * if either of the player is under check, shows a message.
     * */
    private void clicked(int x, int y)
    {
        if(this.gameComplete)	//if game is complete, don't let anyone play.
            return;

        int col = (x-x0) / rowLen;
        int row = (y-y0) / colLen;

        board.print();

        if(board.isCheckMate(Board.White))
        {	this.checkMate(Board.White);
            this.gameComplete = true;
        }

        if(board.isCheckMate(Board.Black))
        {	this.checkMate(Board.Black);
            this.gameComplete = true;
        }

    }


    private class MouseHandler implements MouseListener, MouseMotionListener
    {
        @Override
        public void mouseDragged(MouseEvent e)
        {}

        @Override
        public void mouseMoved(MouseEvent e)
        {}

        @Override
        public void mouseClicked(MouseEvent e)
        {
            clicked(e.getX(), e.getY());
        }

        @Override
        public void mousePressed(MouseEvent e)
        {}

        @Override
        public void mouseReleased(MouseEvent e)
        {}

        @Override
        public void mouseEntered(MouseEvent e)
        {}

        @Override
        public void mouseExited(MouseEvent e)
        {}
    }

}

