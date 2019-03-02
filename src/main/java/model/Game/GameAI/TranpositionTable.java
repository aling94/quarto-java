package model.Game.GameAI;

import model.Game.Game;

import java.util.HashMap;
import java.util.Map;
import static model.Game.GameAI.TranpositionTable.TTFlag.*;

/**
 * Class for logging gamestates when doing a search
 */
public class TranpositionTable
{
    enum TTFlag {LOWER, UPPER, EXACT}

    private Map<String, TTEntry> table = new HashMap<>();

    /**
     * Struct representing a gamestate entry
     */
    public class TTEntry
    {
        public final int value;
        public final int depth;
        public final TTFlag flag;

        public TTEntry(int v, int d, TTFlag f)
        {
            value = v;
            depth = d;
            flag = f;
        }
    }

    /**
     * Adds a gamestate entry to the table
     * @param g Game
     * @param depth int
     * @param alphaPrior int
     * @param beta int
     * @param bestScore int
     */
    public void add(Game g, int depth, int alphaPrior, int beta, int bestScore)
    {
        TTFlag flag;
        if(bestScore <= alphaPrior) flag = UPPER;
        else if (bestScore >= beta) flag = LOWER;
        else flag = EXACT;
        TTEntry entry = new TTEntry(bestScore, depth, flag);
        table.put(g.boardState(), entry);
    }

    /**
     * Retrieves an entry from the table.
     * @param g Game
     * @return TTEntry
     */
    public TTEntry get(Game g)
    {
        return table.get(g.boardState());
    }

    /**
     * Resets the table
     */
    public void clear()
    {
        table.clear();
    }
}
