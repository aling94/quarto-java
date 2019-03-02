package model.Game.GameAI;

import model.Game.Game;
import model.Game.Quarto;
import model.Piece;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class RandomAITest
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
        q.setAI(new RandomAI(), 1);
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
     * Runs a couple turns against the AI. Since this AI is making random moves, we can only check that pieces are
     * being placed and the turn is switching
     */
    @Test
    public void testRunTurnUpdatesBoard() throws NoSuchFieldException, IllegalAccessException
    {
        List<Piece> frees = (List) getInnerField(q, "frees");
        List<Piece> actives = (List) getInnerField(q, "actives");
        // Give the AI the first piece
        q.runTurn(-1,-1, frees.get(0));
        assertEquals(1, q.getTurn());
        assertEquals(15, frees.size());
        // Run with totally invalid inputs. AI will overwrite these with its own moves.
        q.runTurn(-1,-1, null);
        assertEquals(0, q.getTurn());
        assertEquals(1, actives.size());
        assertEquals(14, frees.size());
        assertNotNull(getInnerField(q, "nextPick"));
    }
}
