package net.htlgkr.berghammert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;
import java.util.concurrent.CopyOnWriteArrayList;

public class Movement
{
    private Board board;
    private HashMap<Cell, Figure> onCell;
    private HashMap<Figure, Cell> cellOf;
    private HashMap<Figure, ArrayList<Cell>> moves;
    private Stack<Move> pastMoves;
    public static final String castlingMove = "Castling", normalMove= "normal";
    public static final String promoteMove = "promote";
    private static final String kingSideCastle = "KSC", queenSideCastle = "QSC";
    private HashMap<Figure, Integer> movesCount;

    public Movement(Board board)
    {

        this.board = board;
        onCell = new HashMap<>();
        cellOf = new HashMap<>();
        moves = new HashMap<>();
        pastMoves = new Stack<>();
        movesCount = new HashMap<>();

        for(char row=Board.rowMin; row<=Board.rowMax; row++)
        {
            for(char col=Board.colMin; col<=Board.colMax; col++)
            {
                onCell.put(board.getCellAt(row, col), null);
            }
        }
    }

    public Stack<Move> getPastMoves()
    {
        return this.pastMoves;
    }

    /**
     * Undo the last move.
     * Reincarnate the killed piece.
     * */
    public void undoMove()
    {
        if(pastMoves.isEmpty())
            return;
        Move lastMove = pastMoves.peek();
        pastMoves.pop();

        Figure onSource = lastMove.getSourcePiece();
        Figure onDestination = lastMove.getDestinationPiece();
        Cell source = lastMove.getSource();
        Cell destination = lastMove.getDestination();

        board.reincarnate(onDestination);

        if(lastMove.moveType.equals(promoteMove))
        {
            //reincarnate and demote the promoted pawn.
            board.reincarnate(onSource);
            board.kill(lastMove.getQueen());
        }

        this.put(onDestination, destination);
        this.put(onSource, source);
        //System.out.println("Undo called :| ");
        movesCount.put(onSource, movesCount.get(onSource)-1);
        this.recomputeMoves(onDestination);
        this.recomputeMoves(onSource);
        if(lastMove.moveType.equals(castlingMove) && onSource instanceof Rook)
        {	//If it was a castling move, and rook was moved,
            //call undo once more to undo the castling of King too.
            undoMove();
        }
    }




    public Move moveTo(Figure ownPiece, Cell dest)
    {
        return this.moveTo(cellOf.get(ownPiece), dest);
    }

    public boolean playMove(String from, String to)
    {
        if(from == null || to == null || from.length() != 2 || to.length() != 2)
            return false;
        Cell fromCell = board.getCellAt(from.charAt(1), from.charAt(0));
        Cell toCell = board.getCellAt(to.charAt(1), to.charAt(0));
        Move result = this.moveTo(fromCell, toCell);

        if(result != null)
            board.flipTurn();

        return (result != null);
    }

    public Move moveTo(Cell from, Cell to)
    {
        System.out.println(to + " "+ this.board + " "+ from + " ");
        System.out.println(board.isKilled(onCell.get(from)));

        Figure pieceToMove = onCell.get(from);

        if( pieceToMove instanceof Pawn && (to.row == Board.rowMax ||
                to.row == Board.rowMin)	&& canMoveTo(pieceToMove, to) )
        {
            Move move = this.promotePawn((Pawn)pieceToMove, from, to);
            if(move != null)
            {	return move;
            }
        }

        if( pieceToMove instanceof King && canMoveTo(pieceToMove, to)
                && Math.abs(to.col-from.col)==2)
        {
            Move move =  castle((King)pieceToMove, to);
            if(move != null)
            {	return move;
            }
        }

        if(canMoveTo(pieceToMove, to))
        {
            Move move = new Move(from, to, pieceToMove, onCell.get(to),
                    normalMove);
            pastMoves.add(move);

            onCell.put(from, null);
            if(onCell.get(to) != null)
                board.kill(onCell.get(to));
            onCell.put(to, pieceToMove);
            cellOf.put(pieceToMove, to);
            movesCount.put(pieceToMove, movesCount.get(pieceToMove)+1);

            return move;
        }
        else
            return null;
    }

    private Move castle(King king, Cell to)
    {
        if(onCell.get(to) != null)
            assert(false);

        Cell from = cellOf.get(king);
        char kingRow = from.row;
        Move move = new Move(from, to, king, null, castlingMove);
        pastMoves.add(move);

        onCell.put(from, null);
        onCell.put(to, king);
        cellOf.put(king, to);
        movesCount.put(king, movesCount.get(king)+1);

        Rook rook;
        Cell rookDest;
        if(to.col > from.col)
        {
            rook = (Rook) onCell.get(board.getCellAt(kingRow, Board.colMax));
            rookDest = board.getCellAt(kingRow, Board.colMax-2);
        }
        else
        {
            rook = (Rook) onCell.get(board.getCellAt(kingRow, Board.colMin));
            rookDest = board.getCellAt(kingRow, Board.colMin+3);
        }
        Cell rookCell = cellOf.get(rook);

        if(onCell.get(rookDest) != null)
            assert(false);

        Move move2 = new Move(rookCell, rookDest, rook, null, castlingMove);
        pastMoves.add(move2);

        onCell.put(rookCell, null);
        onCell.put(rookDest, rook);
        cellOf.put(rook, rookDest);
        movesCount.put(rook, movesCount.get(rook)+1);

        return move;
    }

    private Move promotePawn(Pawn pieceToMove, Cell from, Cell to)
    {
        Move move = new Move(from, to, pieceToMove, onCell.get(to),
                promoteMove);
        pastMoves.add(move);

        onCell.put(from, null);
        cellOf.put(pieceToMove, null);

        board.kill(onCell.get(to));
        //movesCount.remove(pieceToMove);	//pawn is now to be destructed.
        try
        {
            Queen newQueen = new Queen(pieceToMove.colour);
            board.promotePawn((Pawn)pieceToMove, newQueen, to);
            move.setQueen(newQueen);
            onCell.put(to, newQueen);
            cellOf.put(newQueen, to);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return move;
    }


    private ArrayList<Cell> getRookMoves(Figure piece)
    {


        ArrayList<Cell> rookMoves = new ArrayList<>();

        rookMoves.addAll(this.movesInDir(piece, 1, 0));
        rookMoves.addAll(this.movesInDir(piece, -1, 0));


        rookMoves.addAll(this.movesInDir(piece, 0, 1));
        rookMoves.addAll(this.movesInDir(piece, 0, -1));
        return rookMoves;
    }

    /**
     * A bishop can move only diagonally, hence
     * sum or difference of (row, col) of current and
     * destination cell must be the same.
     * */
    private ArrayList<Cell> getBishopMoves(Figure piece)
    {
        if(!(piece instanceof Bishop || piece instanceof Queen)
                || piece == null)
            return null;

        ArrayList<Cell> bishopMoves = new ArrayList<>();

        bishopMoves.addAll(this.movesInDir(piece, 1, -1));
        bishopMoves.addAll(this.movesInDir(piece, -1, 1));


        bishopMoves.addAll(this.movesInDir(piece, 1, 1));
        bishopMoves.addAll(this.movesInDir(piece, -1, -1));
        return bishopMoves;
    }


    private ArrayList<Cell> getKnightMoves(Knight knight)
    {
        if(knight == null)
            return null;

        ArrayList<Cell> knightMoves = new ArrayList<>();
        Cell currentPos = cellOf.get(knight);

        final char cr= currentPos.row, cc= currentPos.col;
        Cell possibleCells[]=
                {
                        board.getCellAt((char)(cr-2), (char)(cc-1)),
                        board.getCellAt((char)(cr-2), (char)(cc+1)),
                        board.getCellAt((char)(cr-1), (char)(cc-2)),
                        board.getCellAt((char)(cr-1), (char)(cc+2)),
                        board.getCellAt((char)(cr+1), (char)(cc-2)),
                        board.getCellAt((char)(cr+1), (char)(cc+2)),
                        board.getCellAt((char)(cr+2), (char)(cc-1)),
                        board.getCellAt((char)(cr+2), (char)(cc+1)),
                };
        for(Cell cell : possibleCells)
        {
            if(this.isValidMove(knight, cell))
                knightMoves.add(cell);
        }
        return knightMoves;
    }



    private ArrayList<Cell> getKingMoves(King king)
    {
        if(king == null)
            return null;

        ArrayList<Cell> kingMoves = new ArrayList<>();
        Cell currentPos = cellOf.get(king);

        final char cr= currentPos.row, cc= currentPos.col;
        for(char row = (char)(cr-1); 	row<=cr+1; row++)
        {
            for(char col=(char)(cc-1); 	col<=cc+1; col++)
            {
                if(row == cr && col == cc)
                    continue;
                Cell dest = board.getCellAt(row, col);
                if(this.isValidMove(king, dest))
                {

                    Figure onDest= onCell.get(dest);
                    onCell.put(currentPos, null);
                    onCell.put(dest, king);
                    cellOf.put(king, dest);
                    board.kill(onDest);

                    boolean isValid = !isUnderAttack(dest, king.colour);
                    if(isValid)
                        kingMoves.add(dest);

                    this.put(king, currentPos);
                    this.put(onDest, dest);
                    board.reincarnate(onDest);
                }
            }
        }
        this.moves.put(king, kingMoves);
        return kingMoves;
    }


    private ArrayList<Cell> getPawnMoves(Pawn pawn)
    {
        ArrayList<Cell> pawnMoves = new ArrayList<Cell>();

        Cell currentPos = this.cellOf.get(pawn);

        if(currentPos == null || board.isKilled(pawn))
            return pawnMoves;

        //Case 1: Normal move : 1 cell forward.
        if(this.colourAt((char)(currentPos.row + pawn.dir),
                currentPos.col)	== null)
        {
            pawnMoves.add(board.getCellAt((char)(currentPos.row + pawn.dir),
                    currentPos.col));
        }

        //Case 2: killing move.

        if(	Board.opposite(pawn.colour).equals(
                this.colourAt((char)(currentPos.row + pawn.dir),
                        (char)(currentPos.col-1))) )
        {
            pawnMoves.add(board.getCellAt((char)(currentPos.row + pawn.dir),
                    (char)(currentPos.col-1)));
        }
        if(	Board.opposite(pawn.colour).equals(
                this.colourAt((char)(currentPos.row + pawn.dir),
                        (char)(currentPos.col+1))) )
        {
            pawnMoves.add(board.getCellAt((char)(currentPos.row + pawn.dir),
                    (char)(currentPos.col+1)));
        }

        //Case 3: initial move.

        if( currentPos.equals(pawn.orig) &&
                this.colourAt((char)(currentPos.row + 2*pawn.dir),
                        currentPos.col)	== null &&
                this.colourAt((char)(currentPos.row + pawn.dir),
                        currentPos.col)	== null)
        {
            pawnMoves.add(board.getCellAt((char)(currentPos.row + 2*pawn.dir),
                    currentPos.col));
        }
        return pawnMoves;
    }


    private ArrayList<Cell> recomputeMoves(Figure piece)
    {
        if(piece == null)
            return null;

        ArrayList<Cell> newMoves = new ArrayList<Cell>();
        if(piece instanceof Queen || piece instanceof Rook)
        {
            newMoves.addAll(this.getRookMoves(piece));
        }

        if(piece instanceof Queen || piece instanceof Bishop)
        {
            newMoves.addAll(this.getBishopMoves(piece));
        }

        if(piece instanceof Knight)
        {
            newMoves.addAll(this.getKnightMoves((Knight) piece));
        }

        if(piece instanceof Pawn)
        {
            newMoves.addAll(this.getPawnMoves((Pawn)piece));
        }

        if(piece instanceof King)
        {
            newMoves.addAll(this.getKingMoves((King) piece));
            King king = (King)piece;
            if(canCastle(king, Movement.kingSideCastle)
                    && !isUnderCheck(king.colour))
            {
                newMoves.add(getCastlingMove(king, Movement.kingSideCastle));
            }
            if(canCastle(king, Movement.queenSideCastle)
                    && !isUnderCheck(king.colour))
            {
                newMoves.add(getCastlingMove(king, Movement.queenSideCastle));
            }
        }
        this.moves.put(piece, newMoves);
        return newMoves;
    }

    private Cell getCastlingMove(King king, String castleSide)
    {
        if(king == null || board.isKilled(king) || castleSide == null)
            return null;
        char kingRow= cellOf.get(king).row;
        if( castleSide.equals(Movement.kingSideCastle) )
        {
            return board.getCellAt(kingRow, Board.colMax-1);
        }
        else
        {
            return board.getCellAt(kingRow, Board.colMin+2);
        }
    }


    private boolean canCastle(King king, String castleSide)
    {
        if(king == null || board.isKilled(king) || movesCount.get(king)!=0
                || castleSide == null)
            return false;

        if(isUnderCheck(king.colour))
            return false;

        char kingRow= cellOf.get(king).row;
        if(castleSide.equals(kingSideCastle))
        {
            Cell h1orH8 = board.getCellAt(kingRow, Board.colMax);
            Figure rook = onCell.get(h1orH8);

            if(rook == null || board.isKilled(rook) ||
                    !(rook instanceof Rook) || movesCount.get(rook)!=0)
                return false;

            Cell g1orG8 = board.getCellAt(kingRow, Board.colMax-1);
            Cell f1orF8 = board.getCellAt(kingRow, Board.colMax-2);
            if(onCell.get(g1orG8)!= null || onCell.get(f1orF8)!=null ||
                    this.isUnderAttack(g1orG8, king.colour) ||
                    this.isUnderAttack(f1orF8, king.colour) )
                return false;
            else
                return true;
        }
        else
        {
            Cell a1orA8 = board.getCellAt(kingRow, Board.colMin);
            Figure rook = onCell.get(a1orA8);

            if(rook == null || board.isKilled(rook) ||
                    !(rook instanceof Rook) || movesCount.get(rook)!=0)
                return false;

            Cell d1orD8 = board.getCellAt(kingRow, Board.colMin+3);
            Cell c1orC8 = board.getCellAt(kingRow, Board.colMin+2);
            Cell b1orB8 = board.getCellAt(kingRow, Board.colMin+1);

            if( onCell.get(d1orD8)!= null || onCell.get(c1orC8)!=null ||
                    onCell.get(b1orB8)!= null ||
                    this.isUnderAttack(d1orD8, king.colour) ||
                    this.isUnderAttack(c1orC8, king.colour) )
                return false;
            else
                return true;
        }
    }

    private boolean isValidMove(Figure piece, Cell dest)
    {
        if(dest == null)
            return false;

        if(this.colourAt(dest) != piece.getColour())
            return true;
        else
            return false;
    }

    /**
     * This method doesn't modify anything.
     * It just checks whether destination cell is contained in
     * the list of moves.
     *
     * @return true if this piece can move to destination cell,
     * Returns false otherwise.
     * Returns false if either of the arguments are null.
     * */
    public boolean canMoveTo(Figure figure, Cell to)
    {
        if(to == null || figure == null || board == null)
            return false;


        ArrayList<Cell> movesOfThisPiece = this.recomputeMoves(figure);
        moves.put(figure, movesOfThisPiece);

        return movesOfThisPiece.contains(to);
    }


    public boolean canSelect(Cell cell, String playerColour)
    {
        if(cell == null || playerColour == null)
            return false;

        Figure piece = onCell.get(cell);
        return (piece != null && piece.getColour().equals(playerColour));
    }

    /**
     * @return all moves of the piece on this cell.
     * */
    public ArrayList<Cell> getAllMoves(Cell cell)
    {
        return this.getAllMoves(onCell.get(cell));
    }

    /**
     * @return all moves of this piece.
     */
    public ArrayList<Cell> getAllMoves(Figure piece)
    {
        if(piece == null || board.isKilled(piece) || cellOf.get(piece)==null)
            return null;

        this.recomputeMoves(piece);
        ArrayList<Cell> allMoves = this.moves.get(piece);
        ArrayList<Cell> validMoves = new ArrayList<Cell>();
        for(Cell dest: allMoves)
        {
            if(!(piece instanceof King))
            {

                Cell from = cellOf.get(piece);
                Figure onDestination = onCell.get(dest);
                onCell.put(from, null);
                board.kill(onDestination);
                this.put(piece, dest);
                if(!isUnderCheck(piece.colour))
                {
                    validMoves.add(dest);
                }
                this.put(onDestination, dest);
                this.put(piece, from);
                board.reincarnate(onDestination);

            }
            else
            {
                validMoves.add(dest);
            }
        }

        return validMoves;
    }

    /**
     * @return the colour of the piece
     * on the cell with given (row, column).
     **/
    public String colourAt(char row, char col)
    {
        Cell cell= board.getCellAt(row, col);
        if(cell == null)
            return null;

        Figure onThisCell = onCell.get(cell);
        //System.out.println(onThisCell);
        if(onThisCell == null)
            return null;
        else
            return onThisCell.getColour();
    }

    /**
     * @return the colour of the piece
     * on the cell equivalent to the parameter cell.
     **/
    public String colourAt(Cell dest)
    {
        return this.colourAt(dest.row, dest.col);
    }

    public Figure getPieceOn(Cell cell)
    {
        return onCell.get(cell);
    }

    /**
     * @return true iff current player has got a check-mate.
     * Returns false if the king of this player has at least one move,
     * or it is not under check.
     * */
    public boolean isCheckMate(String playerColour)
    {
        if(!this.isUnderCheck(playerColour))
            return false;

        King king = board.getKing(playerColour);
        this.recomputeMoves(king);
        int size = moves.get(king).size();

        if(size!=0)
            return false;
        else
        {
            //Try moving some piece of current player to see if it can
            //avoid the check.
            //If there is any such move, return false.
            //Otherwise return true.
            CopyOnWriteArrayList<Figure> ownPieces = board.getPieces(playerColour);

            for(Figure piece: ownPieces)
            {
                if(board.isKilled(piece))
                    continue;
                ArrayList<Cell> moveList = moves.get(piece);
                Cell thisCell = cellOf.get(piece);
                for(Cell dest: moveList)
                {
                    Move move = this.moveTo(thisCell, dest);
                    boolean lifeSavingMove = !(this.isUnderCheck(playerColour));
                    if(move != null)
                        this.undoMove();
                    if(lifeSavingMove == true)
                        return false;
                }
            }

            return true;
        }
    }

    /**
     * @return true iff the king of given colour is under check.
     * Returns false otherwise.
     * */
    public boolean isUnderCheck(String playerColour)
    {
        King king = board.getKing(playerColour);
        if(king == null)
            return false;
        if(!this.isUnderAttack(cellOf.get(king), king.colour))
            return false;
        else
        {
            return true;
        }
    }


    /**
     * @return an ArrayList of Cells from current cell,
     * in the the direction of the vector (rowDir, colDir),
     * to which this piece can move.
     * */
    private ArrayList<Cell> movesInDir(Figure figure, int rowDir, int colDir)
    {
        ArrayList<Cell> listOfMoves = new ArrayList<Cell>();
        Cell currentPos = cellOf.get(figure);
        char row =(char)(currentPos.row + rowDir);
        char col =(char)(currentPos.col + colDir);
        String pieceColour = figure.getColour();
        for(; row<=Board.rowMax && col<=Board.colMax &&
                row>=Board.rowMin && col>=Board.colMin; row+=rowDir, col+=colDir)
        {
            if(pieceColour.equals(this.colourAt(row, col)))
            {
                break;
            }
            else if(Board.opposite(pieceColour).equals(this.colourAt(row, col)))
            {
                listOfMoves.add(board.getCellAt(row, col));
                break;
            }

            else
                listOfMoves.add(board.getCellAt(row, col));
        }
        return listOfMoves;
    }

    /**
     * Sets the given piece on the given cell.
     * If piece is null, empties the cell.
     * If piece is not null, sets moveCount of that piece to zero, and
     * 		adds that piece to the board's pieces' list too.
     * */
    private void put(Figure piece, Cell cell)
    {
        if(cell == null)
            return;
        if(piece != null)
        {	cellOf.put(piece, cell);
            if(!board.getPieces(piece.colour).contains(piece))
            {
                try
                {
                    board.construct(piece, cell);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
        onCell.put(cell, piece);
    }

    public void construct(Figure piece, Cell cell)
    {
        this.put(piece, cell);
        movesCount.put(piece, 0);
    }

    /**
     * This method is to be used only by king piece, to check
     * whether cell dest is under attack by any piece or not.
     *
     * @return true if the cell is under attack by
     * any piece other than the king itself (passed as argument).
     * Returns false if this cells is free from threat.
     * */
    public boolean isUnderAttack(Cell cell, String ownColour)
    {
        if(cell == null)
            return false;

        CopyOnWriteArrayList<Figure> oppositeColourPieces;
        oppositeColourPieces = board.getPieces(Board.opposite(ownColour));
        for(Figure piece: oppositeColourPieces)
        {
            if(board.isKilled(piece))
                continue;
            if( !(piece instanceof King) &&
                    this.canMoveTo(piece, cell) )
            {
                return true;
            }
            else if(piece instanceof King)
            {
                Cell thisCell = cellOf.get(piece);
                int distSquared = (thisCell.row-cell.row)*(thisCell.row-cell.row) + (thisCell.col-cell.col)*(thisCell.col-cell.col);
                if(distSquared <= 2)
                {
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * @return The class of the piece on the given cell.
     * */
    public String getPieceType(Cell cell)
    {
        if(cell == null || onCell.get(cell) == null)
            return null;

        return onCell.get(cell).toString();
    }

    public Class<? extends Figure> getPieceClass(Cell cell)
    {
        if(cell == null || onCell.get(cell) == null)
            return null;
        return onCell.get(cell).getClass();
    }

}