package net.htlgkr.berghammert;

public class Move {
    private final Cell source, destination;
    private final Figure onSource, onDestination;
    public final String moveType;
    private Queen newQueen;

    public Move(Cell source, Cell destination, Figure onSource,
                Figure onDestination, String moveType) {
        this.source = source;
        this.destination = destination;
        this.onSource = onSource;
        this.onDestination = onDestination;
        this.moveType = moveType;
        this.newQueen = null;
    }

    public void setQueen(Queen queen) throws Exception {
        if (this.moveType == Movement.promoteMove)
            this.newQueen = queen;
        else
            throw new Exception();
    }

    public Queen getQueen() {
        return this.newQueen;
    }

    public Cell getSource() {
        return source;
    }

    public Cell getDestination() {
        return destination;
    }

    public Figure getSourcePiece() {
        return onSource;
    }

    public Figure getDestinationPiece() {
        return onDestination;
    }

    @Override
    public String toString() {
        String moveString = "";
        if (moveType.equals(Movement.castlingMove)) {
            if (onSource instanceof King) {
                if (destination.col == Board.colMax - 1) {
                    return "o-o";
                } else {
                    return "o-o-o";
                }
            } else
            {
                if (destination.col == source.col - 2) {
                    return "o-o";
                } else {
                    return "o-o-o";
                }
            }
        } else {
            if (!(onSource instanceof Pawn))
                moveString += onSource.toString().charAt(1);
            if (onDestination != null)
                moveString += "x";
            moveString += destination.toString();
            if (moveType.equals(Movement.promoteMove)) {
                moveString += "(Q)";
            }
            return moveString;
        }
    }
}


