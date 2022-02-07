package net.htlgkr.berghammert;

public class Cell {
    public final char row;
    public final char col;
    private boolean selected;
    private boolean nextMove;

    public Cell(char r, char c) {

        row = r;
        col = c;

        selected = false;
        nextMove = false;
    }


    public void select(boolean x) {
        this.selected = x;
    }

    public void setNextMove(boolean x) {
        nextMove = x;
    }

    public boolean isSelected() {
        return selected;
    }

    public boolean isNextMove() {
        return nextMove;
    }

    @Override
    public String toString() {
        return this.col + "" + this.row;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Cell))
            return false;
        Cell other = (Cell) obj;
        if (other.row == this.row && other.col == this.col)
            return true;
        return false;
    }
}
