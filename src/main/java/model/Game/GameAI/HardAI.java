package model.Game.GameAI;

import model.Game.Game;
import model.Game.Move;
import model.Piece;

import java.util.ArrayList;
import java.util.List;
import static java.lang.Integer.max;
import static java.lang.Integer.min;
import static model.Game.GameAI.TranpositionTable.TTFlag.*;

/**
 * More advanced AI that uses a search tree to look for moves. Would not recommend using on Custom Quarto game without
 * further optimizations.
 */
public class HardAI extends GameAI
{

    private TranpositionTable tt = new TranpositionTable();

    private final int MAX_SCORE =  10;
    private final int MIN_SCORE = -10;


    public Move genMove(Game g)
    {
        int maxDepth = 7 - g.dim();
        if(g.getNextPick() == null) return randomFirstMove(g);
        Move winMove = findWin(g);
        if(winMove != null) return winMove;
        return bestMove(g, maxDepth);
    }

    /**
     * Searches the game tree for the best move. This is where the root call for negamax occurs
     * @param g Game
     * @param maxDepth int
     * @return Move
     */
    private Move bestMove(Game g, int maxDepth)
    {
        long startTime = System.nanoTime();
        List<Move> allMoves = genAllMoves(g);
        int bestScore = MIN_SCORE;
        Move bestMove = null;

        maxDepth += 150 / max(75, allMoves.size());
        System.out.print("Root size: " + allMoves.size() + ", time: ");
        tt.clear();
        for(Move m : allMoves)
        {
            g.makeMove(m, true);
            int score = negamax(g, maxDepth, MIN_SCORE, MAX_SCORE, 1);
            g.undoTurn(true);
            if(score >= bestScore)
            {
                bestScore = score;
                bestMove = m;
            }
        }
        System.out.println((System.nanoTime() - startTime)/1000000000.0);
        return bestMove;

    }

    /**
     * Negamax search algorithm for searching through game states. Uses a Transposition Table to memoize nodes.
     * @param g Game
     * @param depth int
     * @param alpha int
     * @param beta int
     * @param turn int
     * @return int
     */
    private int negamax(Game g, int depth, int alpha, int beta, int turn)
    {
        // Table lookup
        int alphaPrior = alpha;
        TranpositionTable.TTEntry entry = tt.get(g);
        if(entry != null && entry.depth >= depth)
        {
            TranpositionTable.TTFlag flag = entry.flag;
            if(flag == EXACT) return entry.value;
            if(flag == UPPER) alpha = max(alpha, entry.value);
            else beta = min(beta, entry.value);
        }
        // Tree search
        if(depth == 0) return turn * countLines(g);
        if(g.winner() != -1) return 1;
        int best = MIN_SCORE;
        for(Move mv : genAllMoves(g))
        {
            g.makeMove(mv, true);
            int score = -negamax(g, depth-1, -beta, -alpha, -turn);
            g.undoTurn(true);
            best = max(best, score);
            alpha = max(alpha, score);
            if(alpha >= beta) break;
        }
        // Memoize the results
        tt.add(g, depth, alphaPrior, beta, best);
        return best;
    }

    /**
     * Helper for counting the number of nearly complete lines.
     * @param g Game
     * @return int
     */
    public static int countLines(Game g)
    {
        int n = g.dim(), count = 0;
        // Count the Horizontal and Vertical lines
        for(int j = 0; j < n; j++)
        {
            List<Piece> hrzn = new ArrayList<>(), vert = new ArrayList<>();
            for (int i = 0; i < n; i++)
            {
                if(!g.isOpen(i,j)) hrzn.add(g.getPiece(i,j));
                if(!g.isOpen(j,i)) vert.add(g.getPiece(j,i));
            }
            if(checkLine(g, hrzn)) count++;
            if(checkLine(g, vert)) count++;
        }
        // Count the diagonals
        List<Piece> diagP = new ArrayList<>(), diagN = new ArrayList<>();
        for(int k = 0; k < n; k++)
        {
            if(!g.isOpen(k,k))     diagP.add(g.getPiece(k,k));
            if(!g.isOpen(k,n-1-k)) diagN.add(g.getPiece(k,n-1-k));
        }
        if(checkLine(g, diagP)) count++;
        if(checkLine(g, diagN)) count++;
        return count;
    }

    /**
     * Checks if a line is nearly complete.
     * @param g Game
     * @param pieces List
     * @return boolean
     */
    public static boolean checkLine(Game g, List<Piece> pieces)
    {
        return pieces.size() >= g.dim()-1 && g.checkAllSimilar(pieces, pieces.remove(0));
    }

    @Override
    public String toString()
    {
        return "Hard";
    }
}
