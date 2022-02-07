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
    private WelcomeWindow welcomeWindow;
    private GameLoaderWindow gameLoaderWindow;
    private GameModeWindow gameModeWindow;
    private SelectPlayerWindow selectPlayerWindow;
    private ChessWindow chessWindow;
    private Player player, player2;
    private String colour, colour2;
    private Setting graphicsHandler;
    private Board board;
    private Movement movement;
    private Game game;

    private int gameMode;
    private JFrame[] windowList;

    public Main()
    {
        welcomeWindow = new WelcomeWindow();
        welcomeWindow.setSize(300, 300);
        welcomeWindow.setVisible(true);

        gameLoaderWindow = new GameLoaderWindow();
        gameLoaderWindow.setSize(300, 300);
        gameLoaderWindow.setVisible(false);

        gameModeWindow = new GameModeWindow();
        gameModeWindow.setSize(300, 300);
        gameModeWindow.setVisible(false);

        board = new Board(true);
        movement = board.getMovement();
        board.print();

        chessWindow = new ChessWindow();
        chessWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        graphicsHandler= new Setting(board, 50, 50, 75, 75, 1);
        chessWindow.add(graphicsHandler);

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
        try
        {
            graphicsHandler.setGameMode(gameMode);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        if(gameMode == 1)
        {
            selectPlayerWindow = new SelectPlayerWindow(1);
            selectPlayerWindow.setSize(300, 300);

            JFrame[] arr = {selectPlayerWindow, chessWindow};
            windowList = arr;
        }
        else
        {
            selectPlayerWindow = new SelectPlayerWindow(2);
            selectPlayerWindow.setSize(300, 300);

            JFrame[] arr = {selectPlayerWindow,	chessWindow};
            windowList = arr;
        }
        windowList[0].setVisible(true);
    }

    private class ChessWindow extends JFrame
    {
        private static final long serialVersionUID = 1L;

        public ChessWindow()
        {
            this.addWindowListener(new ClosingHandler());
        }

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

            return new ImageIcon(icon);
        }

        private class ClosingHandler extends WindowAdapter
        {
            public void windowClosing(WindowEvent e)
            {
                if(Main.this.game == null)
                {
                    try
                    {
                        game = new Game(gameMode, player.toString(),
                                player2.toString(),	colour, colour2,
                                board.getCurrentTurn());
                    }
                    catch (Exception e1)
                    {
                        e1.printStackTrace();
                    }
                }

                game.storeMoves(movement);
                game.storeGame();

                if(board.isCheckMate(colour))
                {
                    player2.won();
                    player.lost();
                }
                else if(board.isCheckMate(colour2))
                {	player.won();
                    player2.lost();
                }

                player.gamePlayed();
                player.storePlayer();
                player2.gamePlayed();
                player2.storePlayer();
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
            super("Name eingeben");
            setLayout(new FlowLayout());

            this.mode = mode;

            allPlayers = Player.getPlayersList();
            Player players[] = allPlayers.toArray(new Player[allPlayers.size()]);

            selectPlayer = new JComboBox<>(players);
            selectPlayer.setEditable(true);
            add(selectPlayer);
            HandlerClass handler = new HandlerClass();
            selectPlayer.addActionListener(handler);

            if(mode == 2)
            {
                JLabel white = new JLabel("White");
                add(white);

                selectPlayer2 = new JComboBox<>(players);
                selectPlayer2.setEditable(true);
                add(selectPlayer2);
                selectPlayer2.addActionListener(handler);
                JLabel black = new JLabel("Black");
                add(black);

                colour = "White";
                colour2 = "Black";
            }
        }

        public void CloseFrame()
        {
            Main.this.nextWindow(this);
            super.dispose();
        }

        private class HandlerClass implements ActionListener
        {
            public void actionPerformed(ActionEvent event)
            {
                String name = selectPlayer.getSelectedItem().toString();
                player = Player.getPlayer(allPlayers, name);
                if(player == null)
                    return;

                if(mode == 2)
                {
                    if(selectPlayer2.getSelectedItem() == null)
                        return;
                    String name2 = selectPlayer2.getSelectedItem().toString();
                    if(name2 == null)
                        return;
                    if(name.equals(name2))
                        return;

                    player2 = Player.getPlayer(allPlayers, name2);
                    if(player2 == null)
                        return;
                }

                CloseFrame();
            }
        }
    }

    private class GameModeWindow extends JFrame
    {
        private static final long serialVersionUID = 1L;

        public GameModeWindow()
        {
            super("Spielmodus auswählen");
            setLayout(new FlowLayout());

            ButtonGroup group = new ButtonGroup();
            JRadioButton multiPlayer = new JRadioButton("2-Spieler Modus");
            add(multiPlayer);
            group.add(multiPlayer);

            multiPlayer.addItemListener(new HandlerClass(2));
        }

        public void CloseFrame()
        {
            super.dispose();
            Main.this.setWindowList();
        }

        private class HandlerClass implements ItemListener
        {
            private int mode;
            public HandlerClass (int x)
            {
                this.mode = x;
            }

            @Override
            public void itemStateChanged(ItemEvent e)
            {
                Main.this.gameMode = this.mode;
                CloseFrame();
            }
        }
    }

    private class WelcomeWindow extends JFrame
    {
        private static final long serialVersionUID = -735673551329590382L;
        private JRadioButton newGame, loadGame;
        public WelcomeWindow()
        {
            super("Willkommen!");
            setLayout(new FlowLayout());

            ButtonGroup group = new ButtonGroup();
            newGame = new JRadioButton("Neues Spiel");
            loadGame = new JRadioButton("Spiel laden");
            add(newGame);
            add(loadGame);

            group.add(loadGame);
            group.add(newGame);

            loadGame.addItemListener(new HandlerClass());
            newGame.addItemListener(new HandlerClass());
        }

        public void CloseFrame()
        {
            this.setVisible(false);
            super.dispose();
        }

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
                    return;
                CloseFrame();
            }
        }
    }

    private class GameLoaderWindow extends JFrame
    {
        private static final long serialVersionUID = 1L;
        private JComboBox<Game> selectGame;
        private ArrayList<Game> allGames;
        private ArrayList<Player> allPlayers;

        public GameLoaderWindow()
        {
            super("Spiel auswählen");
            setLayout(new FlowLayout());

            allPlayers = Player.getPlayersList();
            allGames = Game.getGamesList();
            Game games[] = allGames.toArray(new Game[allGames.size()]);

            selectGame = new JComboBox<>(games);
            selectGame.setEditable(false);
            add(selectGame);
            game = null;

            HandlerClass handler = new HandlerClass();
            selectGame.addActionListener(handler);
        }

        public void CloseFrame()
        {
            this.setVisible(false);
            Main.this.movement = Main.this.game.reloadGame(Main.this.board);
            Main.this.chessWindow.setVisible(true);
            try
            {
                Main.this.graphicsHandler.setGameMode(game.mode);
                if(game.mode == 1)
                {
                    if(player2.toString().equals("Computer"))
                        Main.this.setHumanPlayerColour(game.colour1);
                    else
                        Main.this.setHumanPlayerColour(game.colour2);
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            super.dispose();
        }

        private class HandlerClass implements ActionListener
        {
            public void actionPerformed(ActionEvent event)
            {
                if(Main.this.game != null)
                    return;

                Main.this.game = (Game) selectGame.getSelectedItem();
                if(Main.this.game == null)
                    return;

                Main.this.player = Player.getPlayer(allPlayers, game.player1);
                Main.this.player2 = Player.getPlayer(allPlayers, game.player2);
                Main.this.gameMode = game.mode;
                Main.this.colour = game.colour1;
                Main.this.colour2 = game.colour2;

                CloseFrame();
            }
        }
    }
}

