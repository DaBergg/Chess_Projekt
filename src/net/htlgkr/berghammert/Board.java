package net.htlgkr.berghammert;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.swing.*;

public class Board
{
    public static final char rowMax='8', colMax='h', rowMin='1', colMin='a';
    public static final String White="White", Black = "Black";
    private Movement movement;
    private Cell cells[][];
    private String currentPlayerColour;
    private boolean selected;
    private King whiteKing, blackKing;
    private Cell selectedCell;
    private CopyOnWriteArrayList<Figure> whitePieces, blackPieces,
            killedPieces;

    private void emptyBoard()
    {
        currentPlayerColour = Board.White;
        selected = false;
        selectedCell = null;
        whitePieces = new CopyOnWriteArrayList<>();
        blackPieces = new CopyOnWriteArrayList<>();
        killedPieces = new CopyOnWriteArrayList<>();
        try
        {
            cells = new Cell[8][8];
            for(int i=0; i<8; i++)
            {	for(int j=0; j<8; j++)
            {	cells[i][j]= new Cell((char)(rowMin+i), (char)(colMin+j));
            }
            }

            movement = new Movement(this);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }


    public Board()
    {
        this.emptyBoard();
    }


    public Board(Boolean x)
    {
        this.emptyBoard();
        for(int j=0; j<8; j++)
        {
            try
            {
                this.construct(new Pawn(White, cells[1][j]), cells[1][j]);

                this.construct(new Pawn(Black, cells[6][j]), cells[6][j]);

            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }

        try
        {
            this.construct(new Rook(White), cells[0][0]);
            this.construct(new Rook(White), cells[0][7]);
            this.construct(new Rook(Black), cells[7][0]);
            this.construct(new Rook(Black), cells[7][7]);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        try
        {

            this.construct(new Knight(White), cells[0][1]);
            this.construct(new Knight(White), cells[0][6]);
            this.construct(new Knight(Black), cells[7][1]);
            this.construct(new Knight(Black), cells[7][6]);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        try
        {
            this.construct(new Bishop(White), cells[0][2]);
            this.construct(new Bishop(White), cells[0][5]);
            this.construct(new Bishop(Black), cells[7][2]);
            this.construct(new Bishop(Black), cells[7][5]);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        try
        {

            this.construct(new Queen(White), cells[0][3]);
            this.construct(new Queen(Black), cells[7][3]);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        try
        {

            whiteKing = new King(White);
            blackKing = new King(Black);
            this.construct(whiteKing, cells[0][4]);
            this.construct(blackKing, cells[7][4]);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

    }

    public String getCurrentTurn()
    {
        return this.currentPlayerColour;
    }

    public void flipTurn()
    {
        this.currentPlayerColour = opposite(currentPlayerColour);
    }

    public void construct(Figure piece, Cell cell)
    {

        if(piece.colour.equals(White))
            whitePieces.add(piece);
        else
            blackPieces.add(piece);
        movement.construct(piece, cell);
    }


    public Cell getCellAt(char row, char col)
    {
        if(row>=rowMin && col>=colMin && row<=rowMax && col<=colMax)
            return cells[row-rowMin][col-colMin];
        else
            return null;
    }

    public Cell getCellAt(int row, int col)
    {
        return this.getCellAt((char)row, (char)col);
    }

    public CopyOnWriteArrayList<Figure> getPieces(String colour)
    {
        if(colour.equals(White))
            return whitePieces;
        else
            return blackPieces;
    }

    public static String opposite(String colour)
    {
        if(colour.equals(White))
            return Black;
        else
            return White;
    }

    public void kill(Figure piece)
    {
        if(piece!= null)
        {
            killedPieces.add(piece);
        }
    }

    public boolean isKilled(Figure piece)
    {
        //System.out.println(piece + " isKilled");
        if(piece == null)
            return true;
        return killedPieces.contains(piece);
    }

    public void reincarnate(Figure piece)
    {
        if(piece!= null)
        {
            killedPieces.remove(piece);

        }
    }



    public boolean clicked(int row, int col)
    {
        return this.clicked((char)row, (char)col);
    }

    public boolean clicked(char row, char col)
    {
        Cell thisCell = this.getCellAt(row, col);
        if(thisCell == null)
        {
            System.out.println("Invalid cell in clicked() of Board");
            return false;
        }



        if(!selected)
        {

            if(movement.canSelect(thisCell, currentPlayerColour))
            {
                selectedCell = thisCell;
                selected = true;
                thisCell.select(true);



                ArrayList<Cell> allMoves = movement.getAllMoves(thisCell);
                for(Cell c: allMoves)
                {
                    c.setNextMove(true);
                }
            }

            return false;
        }
        else
        {
            selected = false;
            selectedCell.select(false);
            if(selectedCell != null)
            {
                ArrayList<Cell> allMoves = movement.getAllMoves(selectedCell);
                for(Cell dest: allMoves)
                {
                    dest.setNextMove(false);
                }

                boolean move = false;
                String moveString = null;
                if(moveString == null)
                {
                    Move temp = movement.moveTo(selectedCell, thisCell);
                    if(temp != null)
                        moveString = temp.toString();

                }
                if(moveString != null)//the player has made a move.
                {
                    move = true;
                    this.flipTurn();
                    System.out.println(moveString);
                }
                return move;
            }
            selectedCell = null;
            return false;
        }
    }

    public King getKing(String colour)
    {
        if( colour.equals(White) )
            return whiteKing;
        else
            return blackKing;
    }

    public void promotePawn(Pawn pawn, Queen queen, Cell queenCell)
    {
        kill(pawn);
        try
        {
            this.construct(queen, queenCell);
        }
        catch(Exception e)
        {
            e.printStackTrace();
            System.out.println("Something went wrong in promotePawn() of Board");
        }
    }

    public boolean isCheckMate(String playerColour)
    {
        return movement.isCheckMate(playerColour);
    }

    public boolean isUnderCheck(String playerColour)
    {
        return movement.isUnderCheck(playerColour);
    }

    public String getPieceType(Cell thisCell)
    {
        return movement.getPieceType(thisCell);
    }

    public Class<? extends Figure> getPieceClass(Cell thisCell)
    {
        return movement.getPieceClass(thisCell);
    }

    public Movement getMovement()
    {
        return movement;
    }

}