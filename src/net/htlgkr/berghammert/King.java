package net.htlgkr.berghammert;

public class King extends Figure
{
    public King(String col) throws Exception
    {
        super(col);
    }

    @Override
    public String toString()
    {
        return colour.charAt(0)+"K";
    }

}
