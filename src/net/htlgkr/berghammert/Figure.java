package net.htlgkr.berghammert;

import java.util.LinkedList;

public class Figure {
    int x;
    int y;
    boolean isWhite;
    LinkedList<Figure> f;
    public Figure(int x, int y, boolean isWhite, LinkedList<Figure> f) {
        this.x = x;
        this.y = y;
        this.isWhite = isWhite;
        this.f = f;
        f.add(this);
    }
    public void move(int x, int y) {
        for(Figure f: f) {
            if(f.x == x && f.y == y) {
                f.beat();
            }
        }
    }
    public void beat() {
        f.remove(this);
    }
}
