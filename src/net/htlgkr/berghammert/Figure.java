package net.htlgkr.berghammert;

import java.util.LinkedList;

public abstract class Figure
{
    public final String colour;


    public Figure(String col)
    {
        this.colour = col;
    }

    public abstract String toString();


    public String getColour()
    {
        return colour;
    }

}