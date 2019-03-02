package model.Game.GameAI;

import model.Game.Game;
import model.Game.Move;
import model.Piece;

import java.util.*;

public class NormalAI extends GameAI
{

    public Move genMove(Game g)
    {
        if(g.getNextPick() == null) return randomFirstMove(g);
        Move move = findWin(g);
        if(move != null) return move;
        int[] coords = pickCounterXY(g);
        Piece nextPiece = pickCounterPiece(g);
        return new Move(null, coords[0], coords[1], nextPiece);
    }

    /**
     * Picks a coordinate to place the nextPick piece. Will pick the coords that lines up with the least number of
     * similar pieces.
     * @param g Game
     * @return int[]
     */
    private int[] pickCounterXY(Game g)
    {
        // Compute all the similarity counts on the board
        int n = g.dim();
        int[][] counts = new int[n][n];
        Piece nextPick = g.getNextPick();
        for(int y = 0; y < n; y++)
            for(int x = 0; x < n; x++)
                counts[x][y] = nextPick.countShared(g.getPiece(x,y));

        // Find the xy coord that matches the least number of pieces
        int bestX = -1, bestY = -1, minScore = Integer.MAX_VALUE;
        for(int y = 0; y < n; y++)
            for(int x = 0; x < n; x++)
            {
                if(!g.isOpen(x,y)) continue;
                int score = calcSimScore(counts, x, y);
                if (score < minScore)
                {
                    minScore = score;
                    bestX = x;
                    bestY = y;
                }
            }
        return new int[] {bestX, bestY};
    }

    /**
     * Helper for calculating the similarity score for a coordinate on the board. Similarity is defined as the number of
     * total attributes shared in a horizontal, vertical, and diagonal line from a given coordinate.
     * @param counts int[][]
     * @param x int
     * @param y int
     * @return int[]
     */
    private int calcSimScore(int[][] counts, int x, int y)
    {
        int score = 0, n = counts.length;
        boolean onYeqX = (x==y), onYeqNX = (x+y == n-1);
        for(int i = 0; i < n; i++)
        {
            if (i != y) score += counts[x][i];          // Vertical
            if (i != x)
            {
                score += counts[i][y];                  // Horizontal
                if (onYeqX)  score += counts[i][i];     // y = x
                if (onYeqNX) score += counts[i][n-1-i]; // y = -x
            }
        }
        return score;
    }

    /**
     * Picks the next piece for the opponent. Picks the piece that shares the least number of attributes with the
     * currently active pieces.
     * @param g Game
     * @return Piece
     */
    private Piece pickCounterPiece(Game g)
    {
        List<Piece> actives = new ArrayList<>(g.getActives());
        if(g.getNextPick() != null) actives.add(g.getNextPick());
        Piece mostDiff = null;
        int minScore = Integer.MAX_VALUE;
        for(Piece fp : g.getFrees())
        {
            int simScore = 0;
            for(Piece ap : actives) simScore += fp.countShared(ap);
            if(simScore < minScore)
            {
                mostDiff = fp;
                minScore = simScore;
            }
        }
        return mostDiff;
    }

    @Override
    public String toString()
    {
        return "Normal";
    }
}
