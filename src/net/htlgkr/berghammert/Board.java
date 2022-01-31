package net.htlgkr.berghammert;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.LinkedList;

public class Board extends JPanel{
    public static void main(String[] args) {
        LinkedList<Figure> f = new LinkedList<>();
        BufferedImage all = ImageIO.read(new File("D:\\png.png"));
        Image img[] = new Image[12];
        int id = 0;
        for(int y = 0; y<400; y+= 200){
            for(int x = 0; x<1200; x+= 200){
                img[id] = all.getSubimage(x, y, 200, 200).getScaledInstance(64, 64, BufferedImage.SCALE_SMOOTH);
            id++;
            }
        }
        JFrame frame = new JFrame();
        frame.setBounds(10, 10, 600, 600);
        frame.setUndecorated(true);
        JPanel panel = new JPanel() {
            @Override
            public void paint(Graphics graphics) {
                boolean white = true;
                for(int i = 0; i < 8; i++) {
                    for(int j = 0; j<8; j++) {
                        if(white) {
                            graphics.setColor(Color.LIGHT_GRAY);
                        }
                        else {
                            graphics.setColor(Color.DARK_GRAY);
                        }
                        graphics.fillRect(j*64, i*64, 64, 64);
                        white=!white;
                    }
                    white=!white;
                }
                for(Figure figure: f)
                {
                    int id = 0;
                    if(figure.Name.equalsIgnoreCase("king"))
                    {
                        id = 0;
                    }
                }
            }
        };
        frame.add(panel);
        frame.setDefaultCloseOperation(3);
        frame.setVisible(true);
    }

}
