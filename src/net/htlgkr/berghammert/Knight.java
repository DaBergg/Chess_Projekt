package net.htlgkr.berghammert;

public class Knight extends Figure
{
    public Knight(String col) throws Exception
    {
        super(col);
    }

    @Override
    public String toString()
    {
        return colour.charAt(0)+"N";
    }
}
