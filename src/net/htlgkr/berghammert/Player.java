package net.htlgkr.berghammert;

import javax.swing.*;
import java.io.*;
import java.util.ArrayList;

public class Player implements Serializable {
    private static final long serialVersionUID = 4745428855677338252L;
    private String name;


    public Player(String name) {
        this.name = name;

        System.out.println("Constructed " + name);
    }

    @Override
    public String toString() {
        return "Player{" +
                "name='" + name + '\'' +
                '}';
    }
}
