package net.htlgkr.berghammert;

import javax.swing.*;
import java.awt.*;

public class Board extends JPanel{
    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.setBounds(10, 10, 600, 600);
        JPanel panel = new JPanel() {
            @Override
            public void paint(Graphics graphics) {
                boolean white = true;
                for(int i = 0; i < 8; i++) {
                    for(int j = 0; j<8; j++) {
                        if(white) {
                            graphics.setColor(Color.white);
                        }
                        else {
                            graphics.setColor(Color.black);
                        }
                        graphics.fillRect(j*64, i*64, 64, 64);
                        white=!white;
                    }
                    white=!white;
                }
            }
        };
        frame.add(panel);
        frame.setDefaultCloseOperation(3);
        frame.setVisible(true);
    }

}
