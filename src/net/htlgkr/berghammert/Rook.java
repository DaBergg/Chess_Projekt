package net.htlgkr.berghammert;

public class Rook extends Figure
{
    public Rook(String col)
    {
        super(col);
    }


    @Override
    public String toString()
    {
        return colour.charAt(0)+"R";
    }



}