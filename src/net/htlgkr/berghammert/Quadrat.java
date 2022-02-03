package net.htlgkr.berghammert;

public class Quadrat {
    public final char row;
    public final char colour;
    private boolean selected;
    private boolean nextMove;
    public Quadrat(char r, char c) throws Exception{
        row = r;
        colour = c;
        selected = false;
        nextMove = false;
    }
    public void select(boolean x) {
        this.selected = x;
    }
    public boolean isSelected() {
        return selected;
    }
    public boolean isNextMove() {
        return nextMove;
    }

}
