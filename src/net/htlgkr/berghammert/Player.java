package net.htlgkr.berghammert;

import javax.swing.*;
import java.io.*;
import java.util.ArrayList;

public class Player implements Serializable {
    private static final long serialVersionUID = 4745428855677338252L;
    private String name;
    private Integer gamesPlayed;
    private Integer gamesWon;
    private Integer gamesLost;

    public Player(String name) {
        this.name = name;
        gamesPlayed = 0;
        gamesWon = 0;
        gamesLost = 0;
        System.out.println("Constructed " + name);
    }

    public void won() {
        gamesWon++;
    }

    public void lost() {
        gamesLost++;
    }

    public void gamePlayed() {
        gamesPlayed++;
    }

    public static Player getPlayer(ArrayList<Player> allPlayers, String name) {
        for (Player player : allPlayers) {
            if (player.toString().equals(name)) {
                System.out.println(player.name + " " + player.gamesPlayed + " " + player.gamesWon);
                return player;
            }
        }
        if (name != null)
            return new Player(name);
        else
            return null;
    }

    public String toString() {
        return name;
    }

    public static ArrayList<Player> getPlayersList() {
        Player tempplayer;
        ObjectInputStream input = null;
        ArrayList<Player> players = new ArrayList<Player>();
        try {
            File infile = new File(System.getProperty("user.dir") + File.separator + "playerData.dat");
            input = new ObjectInputStream(new FileInputStream(infile));
            try {
                while (true) {
                    tempplayer = (Player) input.readObject();
                    players.add(tempplayer);
                }
            } catch (EOFException e) {
                input.close();
            }
        } catch (FileNotFoundException e) {
            players.clear();
            return players;
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
        return players;
    }

    public void storePlayer() {
        ObjectInputStream input = null;
        ObjectOutputStream output = null;
        Player tempPlayer;
        File inputFile = null;
        File outputFile = null;
        try {
            inputFile = new File(System.getProperty("user.dir") + File.separator + "playerData.dat");
            outputFile = new File(System.getProperty("user.dir") + File.separator + "tempfile.dat");
        } catch (SecurityException e) {
            e.printStackTrace();
            assert (false);
        }
        boolean playerDoesntExist;
        try {
            if (outputFile.exists() == false)
                outputFile.createNewFile();
            if (inputFile.exists() == false) {
                output = new ObjectOutputStream(new FileOutputStream(outputFile, true));
                output.writeObject(this);
            } else {
                input = new ObjectInputStream(new FileInputStream(inputFile));
                output = new ObjectOutputStream(
                        new FileOutputStream(outputFile));
                playerDoesntExist = true;
                try {
                    while (true) {
                        tempPlayer = (Player) input.readObject();
                        if (tempPlayer.name.equals(this.name)) {
                            output.writeObject(this);
                            playerDoesntExist = false;
                        } else
                            output.writeObject(tempPlayer);
                    }
                } catch (EOFException e) {
                    input.close();
                }
                if (playerDoesntExist)
                    output.writeObject(this);
            }
            inputFile.delete();
            output.close();
            File newf = new File(System.getProperty("user.dir") + File.separator + "playerData.dat");

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
}

