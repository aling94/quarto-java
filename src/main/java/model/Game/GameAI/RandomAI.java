package model.Game.GameAI;

import model.Game.Game;
import model.Game.Move;
import model.Piece;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * A simple AI that chooses a random piece and square from the board.
 */
public class RandomAI extends GameAI
{
    public Move genMove(Game g)
    {
        List<int[]> freeSqrs = new ArrayList<>();
        for(int j = 0; j < g.dim(); j++)
            for (int i = 0; i < g.dim(); i++)
                if(g.isOpen(i,j))
                {
                    int[] sqr = {i,j};
                    freeSqrs.add(sqr);
                }
        int mvX = -1, mvY = -1;
        if(!freeSqrs.isEmpty())
        {
            int[] sqr = freeSqrs.get(new Random().nextInt(freeSqrs.size()));
            mvX = sqr[0];
            mvY = sqr[1];
        }
        List<Piece> frees = g.getFrees();
        Piece pick = (frees.isEmpty())? null : frees.get(new Random().nextInt(frees.size()));
        return new Move(null, mvX, mvY, pick);
    }

    @Override
    public String toString()
    {
        return "Easy (Random)";
    }
}
