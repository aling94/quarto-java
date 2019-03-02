package model.Game.GameAI;

import model.Game.Game;
import model.Game.Move;
import model.Piece;

/**
 * A simple AI that chooses  the first open square and the first free piece listed in the game.
 */
public class FirstAI extends GameAI
{
    public Move genMove(Game g)
    {
        int mvX = -1, mvY = -1;
        for(int j = 0; j < g.dim() && mvY < 0; j++)
            for (int i = 0; i < g.dim() && mvX < 0; i++)
                if(g.isOpen(i,j))
                {
                    mvX = i;
                    mvY = j;
                }
        Piece pick = (g.getFrees().isEmpty())? null : g.getFrees().get(0);
        return new Move(null, mvX, mvY, pick);
    }

    @Override
    public String toString()
    {
        return "Easy (First)";
    }
}
