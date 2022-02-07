package net.htlgkr.berghammert;

public class Pawn extends Figure
{
    public final short dir;

    public final Cell orig;
    public Pawn(String col, Cell cell)  throws Exception
    {	super(col);
        if(col.equals(Board.White))
            dir = 1;
        else
            dir = -1;
        this.orig = cell;
    }


    @Override
    public String toString()
    {
        return colour.charAt(0)+"P";
    }

}
