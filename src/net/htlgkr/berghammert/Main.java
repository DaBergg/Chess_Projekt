package net.htlgkr.berghammert;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

public class Main
{
    private SelectPlayerWindow selectPlayerWindow;
    private ChessWindow chessWindow;
    private String colour, colour2;
    private Setting setting;
    private Board board;
    private Movement movement;
    private Player player, player2;

    private JFrame[] windowList;


    public Main()
    {

        board = new Board(true);
        movement = board.getMovement();
        board.print();

        chessWindow = new ChessWindow();
        chessWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setting= new Setting(board, 50, 50, 75, 75, 1);
        chessWindow.add(setting);

        chessWindow.setSize(800, 800);
        chessWindow.setVisible(false);

    }

    public static void main(String args[])
    {
        new Main();
    }
    private void setHumanPlayerColour(String colour)
    {
        this.colour = colour;

        this.colour2 = Board.opposite(colour);

        try
        {
            movement = board.getMovement();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Finds the next window after this window in the window list,
     * Sets this window as invisible, and next window as visible.
     * */
    public void nextWindow(JFrame window)
    {
        int order=0;

        while(windowList[order] != window)
            order++;
        windowList[order].setVisible(false);
        windowList[order+1].setVisible(true);
    }

    public void setWindowList()
    {
            selectPlayerWindow = new SelectPlayerWindow(2);
            selectPlayerWindow.setSize(300, 300);

            JFrame[] arr = {selectPlayerWindow,	chessWindow};
            windowList = arr;

        windowList[0].setVisible(true);
    }

    /**
     * On closing of the chessWindow, updates the statistics of the player(s),
     * and saves game statistics.
     * (If game is new, creates new game object.
     * Calls storeMoves() and storeGame() of the game object.)
     *
     * Displays a toolbar containing undo button.
     * On clicking undo button, undoes last opponent move and last own move,
     * And, lets the current player play again.
     *
     * TODO: add more buttons and corresponding features.
     *
     * */
    private class ChessWindow extends JFrame
    {
        private static final long serialVersionUID = 1L;


        private ImageIcon getImage(int height, int width, String path)
        {
            BufferedImage img = null;

            try
            {
                img = ImageIO.read(this.getClass().getResource(path));
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

            Image icon = img.getScaledInstance(height, width, Image.SCALE_SMOOTH);
            //Scale the icon to the given size.

            return new ImageIcon(icon);
        }

    }

    /**
     * This window has relevance only when game mode is 1 player.
     * Lets the user select his/her colour.
     * Sets the AI colour to opposite of the player colour.
     * */
    private class SelectColourWindow extends JFrame
    {
        private static final long serialVersionUID = 1L;
        private ButtonGroup colourChoice;

        public SelectColourWindow()
        {
            super("Select colour");
            setLayout(new FlowLayout());

            colourChoice = new ButtonGroup();

            JRadioButton whiteButton = new JRadioButton(Board.White);
            JRadioButton blackButton = new JRadioButton(Board.Black);
            add(whiteButton);
            add(blackButton);
            colourChoice.add(whiteButton);
            colourChoice.add(blackButton);

            whiteButton.addItemListener(new ColourHandler(Board.White));
            blackButton.addItemListener(new ColourHandler(Board.Black));
        }

        /**
         * Closes the current window, and calls nextWindow() from main, to
         * display the next window in the sequence.
         * */
        public void CloseFrame()
        {
            Main.this.nextWindow(this);
            super.dispose();
        }

        /**
         * As soon as user selects the colour, set the colour for it, and
         * close this window.
         * */
        private class ColourHandler implements ItemListener
        {
            private String color;

            public ColourHandler(String s)
            {
                color = s;
            }

            @Override
            public void itemStateChanged(ItemEvent e)
            {
                CloseFrame();
            }
        }
    }


    private class SelectPlayerWindow extends JFrame
    {
        private static final long serialVersionUID = 1L;
        private JComboBox<Player> selectPlayer, selectPlayer2;
        private int mode;
        private ArrayList<Player> allPlayers;

        public SelectPlayerWindow(int mode)
        {
            super("Please select your name");
            setLayout(new FlowLayout());

            this.mode = mode;
            Player players[] = allPlayers.toArray(new Player[allPlayers.size()]);

            selectPlayer = new JComboBox<>(players);
            selectPlayer.setEditable(true);
            add(selectPlayer);


            if(mode == 2)
            {
                JLabel white = new JLabel("White");
                add(white);

                selectPlayer2 = new JComboBox<>(players);
                selectPlayer2.setEditable(true);
                add(selectPlayer2);

                JLabel black = new JLabel("Black");
                add(black);

                colour = "White";
                colour2 = "Black";
            }
        }
        private class WelcomeWindow extends JFrame
        {
            private static final long serialVersionUID = -735673551329590382L;
            private JRadioButton newGame, loadGame;
            public WelcomeWindow()
            {
                super("Welcome to chess!");
                setLayout(new FlowLayout());

                ButtonGroup group = new ButtonGroup();
                newGame = new JRadioButton("New game");
                loadGame = new JRadioButton("Load game");
                add(newGame);
                add(loadGame);

                group.add(loadGame);
                group.add(newGame);

                loadGame.addItemListener(new HandlerClass());
                newGame.addItemListener(new HandlerClass());
            }

            /**
             * Closes the frame.
             * */
            public void CloseFrame()
            {
                this.setVisible(false);
                super.dispose();
            }

            /**
             * If player wants a new game, call game mode window.
             * Otherwise call game loader window.
             * */
            private class HandlerClass implements ItemListener
            {
                @Override
                public void itemStateChanged(ItemEvent e)
                {
                    if(e.getSource() == newGame)
                    {
                        Main.this.gameModeWindow.setVisible(true);
                    }
                    else if(e.getSource() == loadGame)
                    {
                        Main.this.gameLoaderWindow.setVisible(true);
                    }
                    else
                        return;	//nothing changed.
                    CloseFrame();
                }
            }
        }
        /**
         * Closes the current window, and calls nextWindow() from main, to
         * display the next window in the sequence.
         * */
        public void CloseFrame()
        {
            Main.this.nextWindow(this);
            super.dispose();
        }


    }



}
