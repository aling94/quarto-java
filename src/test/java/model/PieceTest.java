package model;

import org.junit.Test;

import static model.Attribute.Att.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PieceTest
{

    private Piece p = new Piece(null);

    /**
     * Tests an empty piece. Should not have any attributes.
     */
    @Test
    public void testEmptyPiece()
    {
        assertTrue(p.getAtts().isEmpty());
    }

    /**
     * Tests adding and attribute and checking that the attribute is present in the piece's attribute set.
     */
    @Test
    public void testAddAtt()
    {
        p.addAtt(Attribute.COLOR, (byte) 0);
        assertTrue(p.hasAtt(BWN));
        p.addAtt(Attribute.SHAPE, (byte) 1);
        assertTrue(p.hasAtt(CIR));
        p.addAtt(Attribute.SIZE, (byte) 1);
        assertTrue(p.hasAtt(SML));
        p.addAtt(Attribute.TOP, (byte) 0);
        assertTrue(p.hasAtt(HLW));
    }

    /**
     * Attempts to add both values from a single attribute type. Piece should just ignore the second attempt.
     */
    @Test
    public void testAddDupedAtt()
    {
        p.addAtt(Attribute.COLOR, (byte) 0);
        assertTrue(p.hasAtt(BWN));
        p.addAtt(Attribute.COLOR, (byte) 1);
        assertFalse(p.hasAtt(YLW));
        p.addAtt(Attribute.SHAPE, (byte) 0);
        assertTrue(p.hasAtt(SQR));
        p.addAtt(Attribute.SHAPE, (byte) 1);
        assertFalse(p.hasAtt(CIR));
    }

    /**
     * Tests whether two pieces are similar after adding attributes to each piece one by one.
     */
    @Test
    public void testIsSimilar()
    {
        // Empty pieces are similar
        Piece other = new Piece(null);
        assertTrue(p.isSimilar(other));

        p.addAtt(Attribute.COLOR, (byte) 0);
        assertFalse(p.isSimilar(other));
        other.addAtt(Attribute.COLOR, (byte) 0);
        assertTrue(p.isSimilar(other));
        // Piece should stay similar even after adding onto Piece p
        p.addAtt(Attribute.SHAPE, (byte) 1);
        p.addAtt(Attribute.SIZE, (byte) 1);
        assertTrue(p.isSimilar(other));
        p.addAtt(Attribute.TOP, (byte) 0);
        assertTrue(p.isSimilar(other));
        // Piece should stay similar even after adding the opposite attributes onto Piece other
        other.addAtt(Attribute.SHAPE, (byte) 0);
        other.addAtt(Attribute.SIZE, (byte) 0);
        assertTrue(p.isSimilar(other));
        other.addAtt(Attribute.TOP, (byte) 1);
        assertTrue(p.isSimilar(other));
    }
}
