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

public class DualAITest
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
        GameAI cpu = new FirstAI();
        q.setAI(cpu, 0);
        q.setAI(cpu, 1);
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
        assertNotNull(cpu[0]);
        assertNotNull(cpu[1]);
    }

    /**
     * Runs a couple turns with 2 AIs. Reflection used to extract the board and check that it is placing pieces
     * as predicted.
     */
    @Test
    public void testRunWith2CPUs() throws NoSuchFieldException, IllegalAccessException
    {
        Board board = (Board) getInnerField(q, "board");
        List<Piece> frees = q.getFrees();
        Piece p0 = frees.get(0),
              p1 = frees.get(1),
              p2 = frees.get(2),
              p3 = frees.get(3);

        // cpu0's turn, picks p0 for the next pick
        q.runTurn(-1,-1, null);
        assertEquals(p0, q.getNextPick());
        // cpu1's turn, places it at (0,0) and picks p1 as next pick
        q.runTurn(-1,-1, null);
        assertEquals(p0, board.get(0,0)); // CPU should have placed it in the first square and pick the first free piece
        assertEquals(p1, q.getNextPick());

        // cpu0's turn, places at (1,0) and picks p2 as next pick
        q.runTurn(-1,-1, null);
        assertEquals(p1, board.get(1,0));
        assertEquals(p2, q.getNextPick());
        // cpu1's turn, places at (2,0) and picks p3 as next pick
        q.runTurn(-1,-1, null);
        assertEquals(p2, board.get(2,0));
        assertEquals(p3, q.getNextPick());
    }

    /**
     * Runs a couple turns with player0 set as a CPU and the player1 as human.
     */
    @Test
    public void testPlayer0CPU() throws NoSuchFieldException, IllegalAccessException
    {
        q.setAI(null, 1);
        Board board = (Board) getInnerField(q, "board");
        List<Piece> frees = q.getFrees();
        Piece p0 = frees.get(0),
              p1 = frees.get(1),
              p2 = frees.get(2),
              p3 = frees.get(3);

        // cpu0's turn, picks p0 for the next pick
        q.runTurn(-1,-1, null);
        assertEquals(p0, q.getNextPick());
        // humans's turn, places it at (3,3) and picks p1 as next pick
        q.runTurn(3,3, p1);
        assertEquals(p0, board.get(3,3)); // CPU should have placed it in the first square and pick the first free piece
        assertEquals(p1, q.getNextPick());

        // cpu0's turn, places at (0,0) and picks p2 as next pick
        q.runTurn(-1,-1, null);
        assertEquals(p1, board.get(0,0));
        assertEquals(p2, q.getNextPick());
        // human's turn, places at (2,0) and picks p3 as next pick
        q.runTurn(2,2, p3);
        assertEquals(p2, board.get(2,2));
        assertEquals(p3, q.getNextPick());
    }

}
