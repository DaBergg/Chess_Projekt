package net.htlgkr.berghammert;


public abstract class Figure
{
    public final String colour;
    public Figure(String col) throws Exception
    {
        this.colour = col;
    }

    public abstract String toString();

    public String getColour()
    {
        return colour;
    }

}