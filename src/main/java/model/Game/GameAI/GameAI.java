package model.Game.GameAI;

import model.Game.Game;
import model.Game.Move;
import model.Piece;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Interface of a Quarto Game AI. Generates a move given the game object and its board.
 */
public abstract class GameAI
{
    public abstract Move genMove(Game g);


    /**
     * Returns a winning move if there is one. Returns null otherwise.
     * @param g Game
     * @return Move
     */
    public static Move findWin(Game g)
    {
        List<Piece> frees = g.getFrees();
        Piece nextPick = g.getNextPick();
        Piece randomP = (frees.isEmpty())? null : frees.get(new Random().nextInt(frees.size()));
        for(int y = 0; y < g.dim(); y++)
            for(int x = 0; x < g.dim(); x++)
                if(g.isOpen(x,y) && g.checkWin(nextPick, x, y))
                    return new Move(nextPick, x, y, randomP);
        return null;
    }


    /**
     * Generates all possible moves for current game.
     * @param g Game
     * @return List<Move>
     */
    public static List<Move> genAllMoves(Game g)
    {
        List<Move> moves = new ArrayList<>();
        Piece placed = g.getNextPick();
        int n = g.dim();
        for(int x = 0; x < n; x++)
            for(int y = 0; y < n; y++)
                if(g.isOpen(x,y))
                {
                    List<Piece> frees = g.getFrees();
                    if(frees.isEmpty()) moves.add(new Move(placed, x, y, null));
                    else for(Piece p : g.getFrees())
                        moves.add(new Move(placed, x, y, p));
                }
        return moves;
    }

    /**
     * Generates a random first move. Returns null if game not actually on the first move.
     * @param g Game
     * @return Move
     */
    public static Move randomFirstMove(Game g)
    {
        List<Piece> frees = g.getFrees();
        if(g.getNextPick() == null)
        {
            Piece randomP = frees.get(new Random().nextInt(frees.size()));
            return new Move(null, -1,-1, randomP);
        }
        return null;
    }
}
