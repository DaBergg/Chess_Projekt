package net.htlgkr.berghammert;

public class Queen extends Figure
{
    public Queen(String col) throws Exception
    {
        super(col);
    }

    @Override
    public String toString()
    {
        return colour.charAt(0)+"Q";
    }

}

