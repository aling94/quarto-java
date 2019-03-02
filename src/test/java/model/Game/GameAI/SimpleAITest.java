package model.Game.GameAI;

import model.Board;
import model.Game.Game;
import model.Game.Quarto;
import model.Piece;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class SimpleAITest
{
    private Quarto q;

    /**
     * Reflection helper. Extracts the inner field of the give name from given the Quarto object
     */
    private Object getInnerField(Quarto q, String fieldname) throws NoSuchFieldException, IllegalAccessException
    {
        Class<?> gClass = Game.class;
        Field field = gClass.getDeclaredField(fieldname);
        field.setAccessible(true);
        return field.get(q);
    }

    /**
     * Setup function. Initializes a new Quarto game with an AI set to always choose the first free piece and open square.
     */
    @Before
    public void setup()
    {
        q = new Quarto();
        q.setAI(new FirstAI(), 1);
    }

    /**
     * Check the the cpu is actually there
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    @Test
    public void testSetAI() throws NoSuchFieldException, IllegalAccessException
    {
        GameAI[] cpu = (GameAI[]) getInnerField(q, "cpu");
        assertNotNull(cpu);
    }

    /**
     * Runs a couple turns against the AI. Reflection used to extract the board and check that it is placing pieces
     * as predicted.
     */
    @Test
    public void testRunTurnUpdatesBoard() throws NoSuchFieldException, IllegalAccessException
    {
        Board board = (Board) getInnerField(q, "board");
        List<Piece> frees = q.getFrees();
        Piece myPick = frees.get(0);
        Piece cpuPick = frees.get(1);
        // Give the AI the first piece
        q.runTurn(-1,-1, myPick);
        // Run with totally invalid inputs. AI will overwrite these with its own moves.
        q.runTurn(-1,-1, null);
        assertEquals(myPick, board.get(0,0)); // CPU should have placed it in the first square and pick the first free piece
        Piece nextPick = (Piece) getInnerField(q, "nextPick");
        assertEquals(cpuPick, nextPick);

        // Try another round
        myPick = frees.get(frees.size()-1);
        cpuPick = frees.get(0);
        q.runTurn(0, 3, myPick);
        q.runTurn(-1,-1, null);
        assertEquals(myPick, board.get(1,0));
        nextPick = (Piece) getInnerField(q, "nextPick");
        assertEquals(cpuPick, nextPick);
    }
}
