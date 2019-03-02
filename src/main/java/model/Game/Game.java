package model.Game;

import model.Attribute;
import model.Attribute.Att;
import model.Board;
import model.Game.GameAI.GameAI;
import model.Piece;

import java.util.*;

/**
 * Represents the basic rules for a game of Quarto.
 */

public abstract class Game
{
    protected List<Attribute> atts = new ArrayList<>();
    protected List<Piece> frees = new ArrayList<>();
    protected List<Piece> actives = new ArrayList<>();
    protected List<Move> moves = new ArrayList<>();
    protected Board board;
    protected GameAI[] cpu = new GameAI[2];
    protected byte turn = 0;
    protected Piece nextPick = null;
    protected boolean gameover = false, draw = false;

    /**
     * Sets the second player to be controlled by an AI. Provide a null to switch the second player back to human-
     * controlled.
     * @param ai GameAI
     * @param playerNum int
     */
    public void setAI(GameAI ai, int playerNum)
    {
        if(playerNum == 0 || playerNum == 1) cpu[playerNum] = ai;
    }

    /**
     * Defines the conditions for a win. Win for standard Quarto is getting 4 in a row of a one or more attributes.
     * Returns true if a win has occurred.
     * @param p Piece
     * @param x int
     * @param y int
     * @return boolean
     */
    public boolean checkWin(Piece p, int x, int y)
    {
        return p != null && checkLines(p,x,y);
    }

    /**
     * Overloaded version that takes a Move object.
     * @param move Move
     * @return boolean
     */
    public boolean checkWin(Move move)
    {
        return move != null && checkWin(move.placed, move.x, move.y);
    }


    /**
     * Runs a single turn of the game. First turn the first player only picks a Piece for the second player. Every
     * turn after that, a player places a Piece and picks another Piece for the other player. Boolean flag specifies
     * whether to force the move without validating a winner.
     * @param x int
     * @param y int
     * @param pick Piece
     * @param forceMove boolean
     */
    public void makeMove(int x, int y, Piece pick, boolean forceMove)
    {
        if(gameover) return;
        boolean firstMove = nextPick == null  && isFree(pick),
                lastMove  = board.isOpen(x,y) && actives.size() == (board.xLength*board.yLength - 1),
                normMove  = board.isOpen(x,y) && isFree(pick);

        if(firstMove) x = y = -1;
        if(lastMove) pick = null;

        if (firstMove || lastMove || normMove)
        {
            moves.add(new Move(nextPick, x, y, pick));
            putPiece(nextPick, x, y);
            if(!forceMove) gameover = checkWin(nextPick, x, y);
            pickPiece(pick);
            if(gameover) turn ^= 1;
            updateState();
        }
    }

    /**
     * Overloaded version that takes a Move object
     * @param m Move
     * @param forceMove boolean
     */
    public void makeMove(Move m, boolean forceMove)
    {
        if(gameover || m == null) return;
        makeMove(m.x, m.y, m.picked, forceMove);
    }

    /**
     * Runs a single turn of the game. First turn the first player only picks a Piece for the second player. Every
     * turn after that, a player places a Piece and picks another Piece for the other player.
     * @param x int
     * @param y int
     * @param pick Piece
     */
    public void runTurn(int x, int y, Piece pick)
    {
        if(gameover) return;
        if(isCPUTurn())
        {
            Move m = cpu[turn].genMove(this);
            x = m.x;
            y = m.y;
            pick = m.picked;
        }
        makeMove(x, y, pick, false);
    }

    /**
     * Undo the last move. Returns a Move representing the last move.
     * @return Move
     * @param forceUndo
     */
    public Move undoTurn(boolean forceUndo)
    {
        if((gameover && !forceUndo) || moves.isEmpty()) return null;
        Move last = moves.remove((moves.size()-1));
        Piece placed = last.placed;
        board.remove(last.x, last.y);
        actives.remove(placed);
        if(nextPick != null) frees.add(nextPick);
        nextPick = placed;
        updateState();
        return last;
    }

    /**
     * Returns the previously made move without undoing.
     * @return Move
     */
    public Move prevMove()
    {
        return (moves.isEmpty())? null : moves.get(moves.size()-1);
    }

    /**
     * Updates the state of the game. If the game isn't over, check if the last move has been played. If not, switch
     * turns.
     */
    protected void updateState()
    {
        if(gameover) return;
        if(actives.size() == (board.xLength*board.yLength)) // Last move played
            gameover = draw = true;
        turn ^= 1;
    }

    /**
     * Puts a piece on the board.
     * @param p Piece
     * @param x int
     * @param y int
     */
    protected void putPiece(Piece p, int x, int y)
    {
        if(p == null) return;
        board.put(p, x, y);
        actives.add(p);
    }

    /**
     * Sets the nextPick piece to be played and removes it from the free Pieces list.
     * @param pick Piece
     */
    protected void pickPiece(Piece pick)
    {
        if(!isFree(pick)) return;
        nextPick = pick;
        frees.remove(pick);
    }

    /*
     * Returns true if the turn is currently a CPU's turns, else false.
     * @return boolean
     */
    public boolean isCPUTurn()
    {
        return turn >= 0 && cpu[turn] != null;
    }

    /**
     * Returns true if the coordinates specifiy an open board square.
     * @param x int
     * @param y int
     * @return boolean
     */
    public boolean isOpen(int x, int y)
    {
        return board.isOpen(x,y);
    }

    /**
     * Returns the piece at the given coordinates, returns null if there's no piece or invalid coordinates.
     * @param x int
     * @param y int
     * @return Piece
     */
    public Piece getPiece(int x, int y)
    {
        return board.get(x, y);
    }

    /**
     * Checks if the Piece is part of the game and is a free piece (not in play). Returns true if piece is free.
     * @param p Piece
     * @return boolean
     */
    public boolean isFree(Piece p)
    {
        return (p != null) && frees.contains(p) && !actives.contains(p);
    }

    /**
     * Returns true if game is on the final turn.
     * @return boolean
     */
    public boolean isLastTurn()
    {
        return actives.size() == (board.xLength*board.yLength - 1);
    }

    /**
     * Returns the list of free pieces.
     * @return List<Piece>
     */
    public List<Piece> getFrees()
    {
        return frees;
    }

    /**
     * Returns the list of active pieces.
     * @return List<Piece>
     */
    public List<Piece> getActives()
    {
        return actives;
    }

    /**
     * Returns a List of free Pieces that have all the attributes specified by the list of attributes
     * @param attributes List
     * @return List
     */
    public List<Piece> findPieces(Att[] attributes)
    {
        List<Piece> ps = new ArrayList<>();
        for(Piece p : frees)
            if(p.hasAtts(attributes)) ps.add(p);
        return ps;
    }

    /**
     * Returns a List of free Pieces that are similar to the specified piece
     * @param piece Piece
     * @return List
     */
    public List<Piece> findSimilarPieces(Piece piece)
    {
        List<Piece> ps = new ArrayList<>();
        for(Piece p : frees)
            if(p.isSimilar(piece)) ps.add(p);
        return ps;
    }

    /**
     * Returns the piece that is next to be placed.
     * @return Piece
     */
    public Piece getNextPick()
    {
        return nextPick;
    }

    /**
     * Returns the number representing the winner of the game, if the game is over. Returns -1 if the game is still in
     * session. 0 = first player, 1 = second player.
     * @return int
     */
    public int winner()
    {
        if(!gameover) return -1;
        return draw? -2 : turn ^ 1;
    }

    /**
     * Returns the turn number. 0 = first player, 1 = second player.
     */
    public byte getTurn()
    {
        return turn;
    }

    /**
     * Returns the side length of the game board used.
     * @return int
     */
    public int dim()
    {
        return board.xLength;
    }

    /**
     * Creates a piece from a binary array. Each bit specifies the attribute value corresponding to the attribute at
     * the same index in atts.
     * @param attNums byte[]
     * @return Piece
     */
    protected Piece pieceFromBinary(byte[] attNums)
    {
        Piece p = new Piece(atts, attNums);
        p.initIcon();
        return p;
    }

    /**
     * Sets up all the piece types.
     */
    protected void setupPieces()
    {
        int n = atts.size();
        genPieces(new byte[n], n);
    }

    /**
     * Helper for recursively generating all the pieces. Essentially generates all possible binary arrays of length
     * n, and calls pieceFromBinary on each generated array.
     * @param attNums int[]
     * @param n int
     */
    protected void genPieces(byte[] attNums, int n)
    {
        if(n < 1) frees.add(pieceFromBinary(attNums));
        else
        {
            attNums[n-1] = 0;
            genPieces(attNums, n-1);
            attNums[n-1] = 1;
            genPieces(attNums, n-1);
        }
    }

    /**
     * Checks if all the given pieces are similar
     * @param pieces List
     * @return boolean
     */
    public boolean checkAllSimilar(List<Piece> pieces, Piece p)
    {
        if(pieces == null || pieces.isEmpty()) return false;
        Set<Att> intersect = new HashSet<>(p.getAtts());
        for(Piece piece : pieces) intersect.retainAll(piece.getAtts());
        return intersect.size() > 0;
    }

    /**
     * Checks to see if there are any wins along a line.
     * @param p Piece
     * @param x int
     * @param y int
     * @return boolean
     */
    protected boolean checkLines(Piece p, int x, int y)
    {
        int n = board.xLength;
        boolean onYeqX = (x==y), onYeqNX = (x+y == n-1);
        List<Piece> vert = new ArrayList<>(),  hrzn = new ArrayList<>(),
                    diagP = new ArrayList<>(), diagN = new ArrayList<>();

        // Add pieces from each line into their respective list
        for(int i = 0; i < n; i++)
        {
            if (i != y && board.hasPiece(x,i)) hrzn.add(board.get(x,i));                // Vertical
            if (i != x)
            {
                if(board.hasPiece(i,y)) vert.add(board.get(i,y));                       // Horizontal
                if (onYeqX && board.hasPiece(i, i))      diagP.add(board.get(i,i));     // y = x
                if (onYeqNX && board.hasPiece(i, n-1-i)) diagN.add(board.get(i,n-1-i)); // y = -x
            }
        }

        // Aggregate the results
        boolean hasWin = false;
        List<Piece>[] lines = new List[]{vert, hrzn, diagP, diagN};
        for(List<Piece> line : lines) hasWin |= ((line.size() >= dim()-1) && checkAllSimilar(line, p));
        return hasWin;
    }

    /**
     * Returns a string representing the board and the next piece to be placed.
     * @return String
     */
    public String boardState()
    {
        StringBuilder sb = new StringBuilder();
        for(int y = 0; y < board.yLength; y++)
            for(int x = 0; x < board.xLength; x++)
            {
                Piece p = board.get(x,y);
                sb.append((p != null)? p.value : 'X');
                sb.append(',');
            }
        sb.append((nextPick != null)? nextPick.value : 'X');
        return sb.toString();
    }
}
