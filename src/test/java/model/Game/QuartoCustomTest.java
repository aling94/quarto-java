package model.Game;

import model.Attribute;
import model.Attribute.Att;
import static model.Attribute.Att.*;

import model.Board;
import model.Piece;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class QuartoCustomTest
{
    private Quarto q;
    private static final Att[] SIZE = {BIG, SML},
            SHAPE = {SQR, CIR},
            COLOR = {BWN, YLW},
            TOP = {HLW, SLD},
            SLASH = {FORW, BACK};

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
     * Helper that constructors an attribute set from the given atts
     */
    private HashSet<Att> attSet(Att a0, Att a1, Att a2, Att a3, Att a4)
    {
        return new HashSet<Att>(Arrays.asList(a0, a1, a2, a3, a4));
    }

    /**
     * Setup function. Initializes a new Quarto game.
     */
    @Before
    public void setup()
    {
        q = new Quarto(Attribute.SLASH);
    }

    /**
     * Attempts to create a custom game with an already existing attribute. Should default to a normal game. Checks
     * that piece roster and board dim correspond to a standard Quarto game.
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    @Test
    public void testAddDupeAttr() throws NoSuchFieldException, IllegalAccessException
    {
        q = new Quarto(Attribute.TOP);
        List<Attribute> atts = (List) getInnerField(q, "atts");
        assertEquals(4, atts.size());
        List<Piece> frees = q.getFrees();
        assertEquals(16, frees.size());
        assertEquals(4, q.dim());
    }

    /**
     * Tests that the list of Attributes being used for the game has been initialized.
     */
    @Test
    public void testAttributeInit() throws NoSuchFieldException, IllegalAccessException
    {
        List<Attribute> atts = (List) getInnerField(q, "atts");
        assertEquals(5, atts.size());
        assertTrue(atts.contains(Attribute.COLOR));
        assertTrue(atts.contains(Attribute.SHAPE));
        assertTrue(atts.contains(Attribute.SIZE));
        assertTrue(atts.contains(Attribute.TOP));
        assertTrue(atts.contains(Attribute.SLASH));
    }

    /**
     * Tests the initialization of the pieces. Checks that all the possible combinations of the 4 attributes are
     * present.
     */
    @Test
    public void testPieceInit()
    {
        List<Piece> frees = q.getFrees();
        assertEquals(32, frees.size());
        Set< Set<Att> > atts = new HashSet<>();
        for(Piece p : frees)
            atts.add(p.getAtts());

        for(Att aSize : SIZE)
            for(Att aShape : SHAPE)
                for(Att aColor : COLOR)
                    for(Att aTop : TOP)
                        for(Att aSlash : SLASH)
                            assertTrue(atts.contains(attSet(aSize, aShape, aColor, aTop, aSlash)));
    }

    /**
     * Runs the first two turns of the game. Picks a piece, and then places a piece. Uses reflection to check that the
     * internal board has placed the piece.
     */
    @Test
    public void testRunTurnUpdatesBoard() throws NoSuchFieldException, IllegalAccessException
    {
        Board board = (Board) getInnerField(q, "board");
        List<Piece> frees = q.getFrees();
        // Only pick a piece on first turn.
        Piece p0 = frees.get(0);
        q.runTurn(0,0, p0);
        // Second turn, place the piece somewhere, and pick a piece for the other player
        Piece p1 = frees.get(0);
        q.runTurn(0,0, p1);
        assertTrue(board.hasPiece(0,0));
        assertEquals(p0, board.get(0,0));
    }

    /**
     * Runs the first two turns of the game. Picks a piece, and then places a piece. Uses reflection to check that the
     * internal lists tracking the pieces in play have been updated. Expect that a piece is removed from frees when picked,
     * but not placed into actives until the next turn specifies where to place it.
     */
    @Test
    public void testRunTurnUpdatesPieceLists() throws NoSuchFieldException, IllegalAccessException
    {
        List<Piece> frees = (List) getInnerField(q, "frees");
        List<Piece> actives = (List) getInnerField(q, "actives");
        // Only pick a piece on first turn.
        Piece p0 = frees.get(0);
        q.runTurn(0,0, p0);
        assertFalse(frees.contains(p0));
        assertFalse(actives.contains(p0));
        // Second turn, place the piece somewhere, and pick a piece for the other player
        Piece p1 = frees.get(0);
        q.runTurn(0,0, p1);
        assertTrue(actives.contains(p0));
        assertFalse(frees.contains(p1));
        assertFalse(actives.contains(p1));
    }

    /**
     * Runs two turns and checks that no winner has been decided yet.
     */
    @Test
    public void testNoWinner()
    {
        List<Piece> frees = q.getFrees();
        Piece p0 = frees.get(0);
        q.runTurn(0,0, p0);
        Piece p1 = frees.get(0);
        q.runTurn(0,0, p1);
        assertEquals(-1, q.winner());
    }

    /**
     * Runs a sequence of turns that results in a win. Pieces are similar in a horizontal line.
     */
    @Test
    public void testWinningGameHorizontal()
    {
        List<Piece> sims = q.findSimilarPieces(q.getFrees().get(0));
        q.runTurn(-1,-1, sims.remove(0));
        q.runTurn(0,0, sims.remove(0));
        q.runTurn(1,0, sims.remove(0));
        q.runTurn(2,0, sims.remove(0));
        q.runTurn(3,0, sims.remove(0));
        q.runTurn(4,0, sims.remove(0));
        assertEquals(1, q.winner());
    }

    /**
     * Runs a sequence of turns that results in a win. Pieces are similar in a vertical line.
     */
    @Test
    public void testWinningGameVertical()
    {
        List<Piece> sims = q.findSimilarPieces(q.getFrees().get(0));
        q.runTurn(-1,-1, sims.remove(0));
        q.runTurn(0,0, sims.remove(0));
        q.runTurn(0,1, sims.remove(0));
        q.runTurn(0,2, sims.remove(0));
        q.runTurn(0,3, sims.remove(0));
        q.runTurn(0,4, sims.remove(0));
        assertEquals(1, q.winner());
    }

    /**
     * Runs a sequence of turns that results in a win. Pieces are similar in a diagonal line, y = x
     */
    @Test
    public void testWinningGameDiag1()
    {
        List<Piece> sims = q.findSimilarPieces(q.getFrees().get(0));
        q.runTurn(-1,-1, sims.remove(0));
        q.runTurn(0,0, sims.remove(0));
        q.runTurn(1,1, sims.remove(0));
        q.runTurn(2,2, sims.remove(0));
        q.runTurn(3,3, sims.remove(0));
        q.runTurn(4,4, sims.remove(0));
        assertEquals(1, q.winner());
    }

    /**
     * Runs a sequence of turns that results in a win. Pieces are similar in a diagonal line, y = -x
     */
    @Test
    public void testWinningGameDiag2()
    {
        List<Piece> sims = q.findSimilarPieces(q.getFrees().get(0));
        q.runTurn(-1,-1, sims.remove(0));
        q.runTurn(4,0, sims.remove(0));
        q.runTurn(3,1, sims.remove(0));
        q.runTurn(2,2, sims.remove(0));
        q.runTurn(1,3, sims.remove(0));
        q.runTurn(0,4, sims.remove(0));
        assertEquals(1, q.winner());
    }

    /**
     * Tests a draw. Simulates playing 14 pieces without winning by using reflection. Plays the last two pieces. Should
     * end in a draw.
     */
    @Test
    public void testDrawGame() throws NoSuchFieldException, IllegalAccessException
    {
        List<Piece> frees = (List) getInnerField(q, "frees");
        List<Piece> actives = (List) getInnerField(q, "actives");
        for(int i = 0; i < 23; i++)
            actives.add(frees.remove(0));
        q.runTurn(-1,-1, frees.get(0));
        q.runTurn(0,0, frees.get(0));
        q.runTurn(1,1, null);
        assertEquals(-2, q.winner());
    }
}
