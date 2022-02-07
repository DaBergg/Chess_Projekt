package net.htlgkr.berghammert;

public class Bishop extends Figure {
    public Bishop(String col) throws Exception {
        super(col);
    }
    @Override
    public String toString() {
        return colour.charAt(0) + "B";
    }
}

