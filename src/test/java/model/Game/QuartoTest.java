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

public class QuartoTest
{
    private Quarto q;
    private static final Att[] SIZE = {BIG, SML},
            SHAPE = {SQR, CIR},
            COLOR = {BWN, YLW},
            TOP = {HLW, SLD};

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
    private HashSet<Att> attSet(Att a0, Att a1, Att a2, Att a3)
    {
        return new HashSet<>(Arrays.asList(a0, a1, a2, a3));
    }

    /**
     * Setup function. Initializes a new Quarto game.
     */
    @Before
    public void setup()
    {
        q = new Quarto();
    }

    /**
     * Test the constructor to see that the internal board and lists have been initialized.
     */
    @Test
    public void testConstructor() throws NoSuchFieldException, IllegalAccessException
    {
        assertNotNull(q);
        assertNotNull(getInnerField(q, "board"));
        assertNotNull(getInnerField(q, "atts"));
        assertNotNull(getInnerField(q, "frees"));
        assertNotNull(getInnerField(q, "actives"));
    }

    /**
     * Tests that the list of Attributes being used for the game has been initialized.
     */
    @Test
    public void testAttributeInit() throws NoSuchFieldException, IllegalAccessException
    {
        List<Attribute> atts = (List) getInnerField(q, "atts");
        assertEquals(4, atts.size());
        assertTrue(atts.contains(Attribute.COLOR));
        assertTrue(atts.contains(Attribute.SHAPE));
        assertTrue(atts.contains(Attribute.SIZE));
        assertTrue(atts.contains(Attribute.TOP));
    }

    /**
     * Tests the initialization of the pieces. Checks that all the possible combinations of the 4 attributes are
     * present.
     */
    @Test
    public void testPieceInit()
    {
        List<Piece> frees = q.getFrees();
        assertEquals(16, frees.size());
        Set<Set<Att>> atts = new HashSet<>();
        for (Piece p : frees)
            atts.add(p.getAtts());

        for (Att aSize : SIZE)
            for (Att aShape : SHAPE)
                for (Att aColor : COLOR)
                    for (Att aTop : TOP)
                        assertTrue(atts.contains(attSet(aSize, aShape, aColor, aTop)));
    }

    /**
     * Helper for testing the findPieces method. Checks the number of pieces outputted against the expected count,
     * and checks that all the pieces do indeed have the listed attributes.
     */
    private void findPieceHelper(int expectedCount, Att[] as)
    {
        List<Piece> ps = q.findPieces(as);
        assertEquals(expectedCount, ps.size());
        for (Piece p : ps) assertTrue(p.hasAtts(as));
    }

    /**
     * Test findPieces with one attribute
     */
    @Test
    public void testFindPiecesSingleAtt()
    {
        findPieceHelper(8, new Att[]{BWN});
        findPieceHelper(8, new Att[]{SQR});
        findPieceHelper(8, new Att[]{HLW});
        findPieceHelper(8, new Att[]{SML});
    }


    /**
     * Test findPieces with two attributes
     */
    @Test
    public void testFindPiecesTwoAtt()
    {
        findPieceHelper(4, new Att[]{BWN, SQR});
        findPieceHelper(4, new Att[]{HLW, BIG});
        findPieceHelper(4, new Att[]{YLW, SLD});
        findPieceHelper(4, new Att[]{SML, CIR});
    }

    /**
     * Test findPieces with three attributes
     */
    @Test
    public void testFindPiecesThreeAtt()
    {
        findPieceHelper(2, new Att[]{BWN, SQR, SLD});
        findPieceHelper(2, new Att[]{HLW, BIG, YLW});
        findPieceHelper(2, new Att[]{BWN, CIR, SLD});
        findPieceHelper(2, new Att[]{SLD, BIG, YLW});
    }

    /**
     * Test findPieces with four attributes
     */
    @Test
    public void testFindPiecesFourAtt()
    {
        findPieceHelper(1, new Att[]{BWN, SQR, SLD, BIG});
        findPieceHelper(1, new Att[]{HLW, BIG, YLW, CIR});
        findPieceHelper(1, new Att[]{YLW, CIR, SLD, BIG});
        findPieceHelper(1, new Att[]{HLW, SML, YLW, SQR});
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
        q.runTurn(0, 0, p0);
        // Second turn, place the piece somewhere, and pick a piece for the other player
        Piece p1 = frees.get(0);
        q.runTurn(0, 0, p1);
        assertTrue(board.hasPiece(0, 0));
        assertEquals(p0, board.get(0, 0));
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
        q.runTurn(0, 0, p0);
        assertFalse(frees.contains(p0));
        assertFalse(actives.contains(p0));
        // Second turn, place the piece somewhere, and pick a piece for the other player
        Piece p1 = frees.get(0);
        q.runTurn(0, 0, p1);
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
        q.runTurn(0, 0, p0);
        Piece p1 = frees.get(0);
        q.runTurn(0, 0, p1);
        assertEquals(-1, q.winner());
    }

    /**
     * Runs a sequence of turns that results in a win. Pieces are similar in a horizontal line.
     */
    @Test
    public void testWinningGameHorizontal()
    {
        List<Piece> sims = q.findSimilarPieces(q.getFrees().get(0));
        assertEquals(15, sims.size());
        q.runTurn(-1, -1, sims.remove(0));
        q.runTurn(0, 0, sims.remove(0));
        q.runTurn(1, 0, sims.remove(0));
        q.runTurn(2, 0, sims.remove(0));
        q.runTurn(3, 0, sims.remove(0));
        assertEquals(0, q.winner());
    }

    /**
     * Runs a sequence of turns that results in a win. Pieces are similar in a vertical line.
     */
    @Test
    public void testWinningGameVertical()
    {
        List<Piece> sims = q.findSimilarPieces(q.getFrees().get(0));
        assertEquals(15, sims.size());
        q.runTurn(-1, -1, sims.remove(0));
        q.runTurn(0, 0, sims.remove(0));
        q.runTurn(0, 1, sims.remove(0));
        q.runTurn(0, 2, sims.remove(0));
        q.runTurn(0, 3, sims.remove(0));
        assertEquals(0, q.winner());
    }

    /**
     * Runs a sequence of turns that results in a win. Pieces are similar in a diagonal line, y = x
     */
    @Test
    public void testWinningGameDiag1()
    {
        List<Piece> sims = q.findSimilarPieces(q.getFrees().get(0));
        assertEquals(15, sims.size());
        q.runTurn(-1, -1, sims.remove(0));
        q.runTurn(0, 0, sims.remove(0));
        q.runTurn(1, 1, sims.remove(0));
        q.runTurn(2, 2, sims.remove(0));
        q.runTurn(3, 3, sims.remove(0));
        assertEquals(0, q.winner());
    }

    /**
     * Runs a sequence of turns that results in a win. Pieces are similar in a diagonal line, y = -x
     */
    @Test
    public void testWinningGameDiag2()
    {
        List<Piece> sims = q.findSimilarPieces(q.getFrees().get(0));
        assertEquals(15, sims.size());
        q.runTurn(-1, -1, sims.remove(0));
        q.runTurn(3, 0, sims.remove(0));
        q.runTurn(2, 1, sims.remove(0));
        q.runTurn(1, 2, sims.remove(0));
        q.runTurn(0, 3, sims.remove(0));
        assertEquals(0, q.winner());
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
        for (int i = 0; i < 14; i++)
            actives.add(frees.remove(0));
        q.runTurn(-1, -1, frees.get(0));
        q.runTurn(0, 0, frees.get(0));
        q.runTurn(1, 1, null);
        assertEquals(-2, q.winner());
        assertEquals(16, q.getActives().size());
    }

    /**
     * Tests an invalid first move by giving the game a null piece. Turn should not switch and piece lists should
     * remain unaffected. The coordinates inputted should not matter, so valid squares still won't run the turn.
     *
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    @Test
    public void testInvalidFirstMove() throws NoSuchFieldException, IllegalAccessException
    {
        assertEquals(0, q.turn);
        q.runTurn(-1, -1, null);
        assertEquals(0, q.turn);
        q.runTurn(0, 0, null);
        assertEquals(0, q.turn);
        List<Piece> frees = (List) getInnerField(q, "frees");
        List<Piece> actives = (List) getInnerField(q, "actives");
        assertEquals(16, frees.size());
        assertEquals(0, actives.size());
    }

    /**
     * Tests an invalid last move by giving the game an invalid coordinate. Turn should not switch and winner should
     * not be decided.
     *
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    @Test
    public void testInvalidLastMove() throws NoSuchFieldException, IllegalAccessException
    {
        List<Piece> frees = (List) getInnerField(q, "frees");
        List<Piece> actives = (List) getInnerField(q, "actives");
        for (int i = 0; i < 14; i++)
            actives.add(frees.remove(0));
        q.runTurn(-1, -1, frees.get(0));
        q.runTurn(0, 0, frees.get(0));
        q.runTurn(-1, -1, null);
        assertEquals(0, q.getTurn());
        assertEquals(-1, q.winner());
    }

    /**
     * Tests an invalid move on a normal game cycle. Invalid coordinates, non-free picks, or both should not change the
     * turn or affect the piece lists. The nextPick piece should also remain unchanged.
     * not be decided.
     *
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    @Test
    public void testInvalidCycle() throws NoSuchFieldException, IllegalAccessException
    {
        List<Piece> frees = (List) getInnerField(q, "frees");
        List<Piece> actives = (List) getInnerField(q, "actives");
        Piece p0 = frees.get(0);
        // Bunch of invalid attempts
        q.runTurn(-1, -1, p0);
        assertEquals(1, q.getTurn());
        q.runTurn(-1, -1, frees.get(0)); // Bad coordinates
        assertEquals(1, q.getTurn());
        q.runTurn(0, 0, p0);             // Non-free piece
        assertEquals(1, q.getTurn());
        q.runTurn(0, 0, null);           // Null piece
        assertEquals(1, q.getTurn());
        q.runTurn(-1, -1, p0);           // Bad coords + non-free
        assertEquals(1, q.getTurn());
        q.runTurn(-1, -1, null);         // Bad coords + null
        assertEquals(1, q.getTurn());
        assertEquals(15, frees.size());
        assertEquals(0, actives.size());

        Piece nextPick = (Piece) getInnerField(q, "nextPick");
        assertEquals(p0, nextPick);
    }


    /**
     * Try to undo with no moves. Should do nothing.
     * Then run and undo the first move. Checks that undo will undo the last move, swap turns,
     * and return the correct Move obj.
     */
    @Test
    public void testUndo0() throws NoSuchFieldException, IllegalAccessException
    {
        assertNull(q.undoTurn(false));
        List<Piece> frees = q.getFrees();
        Piece p0 = frees.get(0);
        q.runTurn(-1, -1, p0);   // First move, pick a piece
        assertEquals(15, frees.size());
        assertEquals(1, q.getTurn());
        assertEquals(p0, getInnerField(q, "nextPick"));

        Move fstMove = q.undoTurn(false);
        assertEquals(16, frees.size());     // Frees regain a piece
        assertEquals(-1, fstMove.x);        // Check all fields of the move
        assertEquals(-1, fstMove.y);
        assertEquals(p0, fstMove.picked);
        assertNull(fstMove.placed);

        assertNull(getInnerField(q, "nextPick"));
        assertEquals(0, q.getTurn());
    }


    /**
     * Run a second move and test undo again.
     * Checks that undo will undo the last move, swap turns, and return the correct Move obj.
     */
    @Test
    public void testUndo1() throws NoSuchFieldException, IllegalAccessException
    {
        List<Piece> frees = q.getFrees();
        Piece p0 = frees.get(0);
        q.runTurn(-1, -1, p0);           // First move, pick a piece
        Piece p1 = frees.get(0);
        q.runTurn(0, 0, frees.get(0));   // Second move, place it at 0,0 and pick the first free piece

        assertEquals(14, frees.size());     // Check frees, turn and next pick
        assertEquals(0, q.getTurn());
        assertEquals(p1, getInnerField(q, "nextPick"));

        Move fstMove = q.undoTurn(false);
        assertEquals(15, frees.size());     // Frees regain a piece
        assertEquals(0, fstMove.x);        // Check all fields of the move
        assertEquals(0, fstMove.y);
        assertEquals(p1, fstMove.picked);
        assertEquals(p0, fstMove.placed);

        assertEquals(p0, getInnerField(q, "nextPick"));
        assertEquals(1, q.getTurn());
    }

    /**
     * Attempts to undo after winning a game. Should not be able to undo.
     */
    @Test
    public void testCannotUndoWin()
    {
        List<Piece> sims = q.findSimilarPieces(q.getFrees().get(0));
        assertEquals(15, sims.size());
        q.runTurn(-1, -1, sims.remove(0));
        q.runTurn(0, 0, sims.remove(0));
        q.runTurn(1, 0, sims.remove(0));
        q.runTurn(2, 0, sims.remove(0));
        q.runTurn(3, 0, sims.remove(0));
        assertEquals(0, q.winner());
        assertNull(q.undoTurn(false));
        assertEquals(4, q.getActives().size());
    }

    /**
     * Attempts to undo after getting a draw. You should not be able to undo.
     */
    @Test
    public void testCannotUndoDraw() throws NoSuchFieldException, IllegalAccessException
    {
        List<Piece> frees = (List) getInnerField(q, "frees");
        List<Piece> actives = (List) getInnerField(q, "actives");
        for (int i = 0; i < 14; i++)
            actives.add(frees.remove(0));
        q.runTurn(-1, -1, frees.get(0));
        q.runTurn(0, 0, frees.get(0));
        q.runTurn(1, 1, null);
        assertEquals(-2, q.winner());
        assertNull(q.undoTurn(false));
        assertEquals(16, q.getActives().size());
    }

}
