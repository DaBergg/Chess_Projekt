package net.htlgkr.berghammert;

import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Stack;

public class Game implements Serializable {
    private static final long serialVersionUID = -456686249905185825L;
    public final String player1, player2, colour1, colour2;
    public final Integer mode;
    private ArrayList<String[]> allMoves;
    private final long startTime;
    private String toPlay;


    public Game(int mode, String player1, String player2, String colour1, String colour2, String toPlay) {

        this.player1 = player1;
        this.player2 = player2;
        this.mode = mode;
        this.colour1 = colour1;
        this.colour2 = colour2;
        this.startTime = System.nanoTime();

        allMoves = new ArrayList<>();
        this.toPlay = toPlay;
    }


    @Override
    public String toString() {
        return player1 + " vs " + player2 + " @ " + startTime;
    }

    public static ArrayList<Game> getGamesList() {
        Game tempGame;
        ObjectInputStream input = null;
        ArrayList<Game> games = new ArrayList<Game>();
        try {
            File infile = new File(System.getProperty("user.dir") + File.separator + "gameData.dat");
            input = new ObjectInputStream(new FileInputStream(infile));
            try {
                while (true) {
                    tempGame = (Game) input.readObject();
                    games.add(tempGame);
                }
            } catch (EOFException e) {
                input.close();
            }
        } catch (FileNotFoundException e) {
            games.clear();
            return games;
        } catch (IOException e) {
            e.printStackTrace();
            try {
                input.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return games;
    }

    public void storeGame() {
        ObjectInputStream input = null;
        ObjectOutputStream output = null;
        Game tempGame;
        File inputFile = null;
        File outputFile = null;
        try {
            inputFile = new File(System.getProperty("user.dir") + File.separator + "gameData.dat");
            outputFile = new File(System.getProperty("user.dir") + File.separator + "tempFile.dat");
        } catch (SecurityException e) {
            e.printStackTrace();
            assert (false);
        }
        boolean gameDoesntExist;
        try {
            if (outputFile.exists() == false)
                outputFile.createNewFile();
            if (inputFile.exists() == false) {
                output = new ObjectOutputStream(
                        new FileOutputStream(outputFile, true));
                output.writeObject(this);
            } else {
                input = new ObjectInputStream(new FileInputStream(inputFile));
                output = new ObjectOutputStream(
                        new FileOutputStream(outputFile));
                gameDoesntExist = true;
                try {
                    while (true) {
                        tempGame = (Game) input.readObject();
                        if (tempGame.equals(this)) {
                            output.writeObject(this);
                            gameDoesntExist = false;
                        } else
                            output.writeObject(tempGame);
                    }
                } catch (EOFException e) {
                    input.close();
                }
                if (gameDoesntExist)
                    output.writeObject(this);
            }
            inputFile.delete();
            output.close();
            File newf = new File(System.getProperty("user.dir") + File.separator + "gameData.dat");

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void storeMoves(Movement movement) {
        Stack<Move> moves = movement.getPastMoves();
        toPlay = Board.White;
        allMoves = new ArrayList<>();
        for (int i = 0; i < moves.size(); i++) {
            String mov[] = new String[2];
            mov[0] = moves.get(i).getSource().toString();
            mov[1] = moves.get(i).getDestination().toString();
            allMoves.add(mov);
            toPlay = Board.opposite(toPlay);
        }
    }

    public Movement reloadGame(Board board) {
        Movement movement = board.getMovement();
        for (String[] move : allMoves) {
            System.out.println(move[0] + " " + move[1]);
            board.print();
            movement.playMove(move[0], move[1]);
        }
        return movement;
    }
}
