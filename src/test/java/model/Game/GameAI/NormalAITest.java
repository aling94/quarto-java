package model.Game.GameAI;

        import model.Board;
        import model.Game.Game;
        import model.Game.Move;
        import model.Game.Quarto;
        import model.Attribute.Att;
        import static model.Attribute.Att.*;
        import model.Piece;
        import org.junit.Before;
        import org.junit.Test;

        import java.lang.reflect.Field;
        import java.util.List;

        import static org.junit.Assert.assertEquals;
        import static org.junit.Assert.assertNotNull;
        import static org.junit.Assert.assertTrue;

public class NormalAITest
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
    }

    /**
     * Finds a free square on the board to place a piece on.
     */
    private int[] findFreeSqr(Board b)
    {
        for(int y = 0; y < b.yLength; y++)
            for(int x = 0; x < b.xLength; x++)
                if(b.isOpen(x,y)) return new int[] {x,y};
        return null;
    }

    /**
     * Runs a couple turns against the AI to see that it is actually executing moves.
     */
    @Test
    public void testRunTurns() throws NoSuchFieldException, IllegalAccessException
    {
        q.setAI(new NormalAI(), 1);
        Board board = (Board) getInnerField(q, "board");
        int[] freeSpot;
        List<Piece> frees = q.getFrees();

        assertEquals(0, q.getTurn());
        q.runTurn(-1,-1,frees.get(0));
        assertEquals(1, q.getTurn());
        assertEquals(15, frees.size());

        q.runTurn(-1,-1, null);
        assertEquals(0, q.getTurn());
        assertEquals(14, frees.size());
        freeSpot = findFreeSqr(board);

        q.runTurn(freeSpot[0],freeSpot[1],frees.get(0));
        assertEquals(1, q.getTurn());
        assertEquals(13, frees.size());

        q.runTurn(-1,-1, null);
        assertEquals(0, q.getTurn());
    }

    /**
     * Checks that the AI will choose the winning move if available. This tests a horizontal win
     */
    @Test
    public void testAIWinsHrzn()
    {
        List<Piece> sims = q.findSimilarPieces(q.getFrees().get(0));
        q.runTurn(-1,-1, sims.remove(0));
        q.runTurn(0,0, sims.remove(0));
        q.runTurn(1,0, sims.remove(0));
        q.runTurn(2,0, sims.remove(0));
        assertEquals(0, q.getTurn());
        q.runTurn(3,3, sims.remove(0));
        assertEquals(1, q.getTurn());
        q.setAI(new NormalAI(), 1);
        q.runTurn(-1,-1, null);
        assertEquals(1, q.winner());
    }

    /**
     * Checks that the AI will choose the winning move if available. This tests a vertical win
     */
    @Test
    public void testAIWinsVert()
    {
        List<Piece> sims = q.findSimilarPieces(q.getFrees().get(0));
        q.runTurn(-1,-1, sims.remove(0));
        q.runTurn(0,0, sims.remove(0));
        q.runTurn(0,0, sims.remove(0));
        q.runTurn(0,1, sims.remove(0));
        q.runTurn(0,2, sims.remove(0));
        assertEquals(0, q.getTurn());
        q.runTurn(3,3, sims.remove(0));
        assertEquals(1, q.getTurn());
        q.setAI(new NormalAI(), 1);
        q.runTurn(-1,-1, null);
        assertEquals(1, q.winner());
    }

    /**
     * Checks that the AI will choose the winning move if available. This tests a diagonal y=x win
     */
    @Test
    public void testAIWinsDiag1()
    {
        List<Piece> sims = q.findSimilarPieces(q.getFrees().get(0));
        q.runTurn(-1,-1, sims.remove(0));
        q.runTurn(0,0, sims.remove(0));
        q.runTurn(1,1, sims.remove(0));
        q.runTurn(2,2, sims.remove(0));
        assertEquals(0, q.getTurn());
        q.runTurn(0,3, sims.remove(0));
        assertEquals(1, q.getTurn());
        q.setAI(new NormalAI(), 1);
        q.runTurn(-1,-1, null);
        assertEquals(1, q.winner());
    }

    /**
     * Checks that the AI will choose the winning move if available. This tests a diagonal y=-x win
     */
    @Test
    public void testAIWinsDiag2()
    {
        List<Piece> sims = q.findSimilarPieces(q.getFrees().get(0));
        q.runTurn(-1,-1, sims.remove(0));
        q.runTurn(3,0, sims.remove(0));
        q.runTurn(2,1, sims.remove(0));
        q.runTurn(1,2, sims.remove(0));
        assertEquals(0, q.getTurn());
        q.runTurn(0,0, sims.remove(0));
        assertEquals(1, q.getTurn());
        q.setAI(new NormalAI(), 1);
        q.runTurn(-1,-1, null);
        assertEquals(1, q.winner());
    }

    /**
     * If the AI cannot win, it should pick the piece least similar to all pieces currently on the board
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    @Test
    public void testAICounterPiece() throws NoSuchFieldException, IllegalAccessException
    {
        Board board = (Board) getInnerField(q, "board");
        List<Piece> actives = q.getActives();
        GameAI cpu = new NormalAI();
        q.setAI(cpu, 1);

        Piece p0 = q.findPieces(new Att[]{BWN, BIG, SQR, SLD}).get(0);
        Piece notP0 = q.findPieces(new Att[]{YLW, SML, CIR, HLW}).get(0);
        q.runTurn(-1,-1, p0);
        Move aiMove = cpu.genMove(q);
        assertEquals(notP0, aiMove.picked);
        q.runTurn(-1,-1, null);
        assertEquals(notP0, q.getNextPick());

        int[] freeSpot = findFreeSqr(board);
        Piece p1 = q.findPieces(new Att[]{YLW, BIG, SQR, SLD}).get(0);
        Piece notP1 = q.findPieces(new Att[]{BWN, SML, CIR, HLW}).get(0);
        q.runTurn(freeSpot[0],freeSpot[1], p1);

        aiMove = cpu.genMove(q);
        assertEquals(notP1, aiMove.picked);
        q.runTurn(-1,-1, null);
        assertEquals(notP1, q.getNextPick());
    }
}
