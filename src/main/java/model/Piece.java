package model;

import model.Attribute.Att;

import javax.swing.*;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Represents a Quarto piece. Pieces can have any number of Attributes. A Quarto piece simply stores the attributes it
 * has.
 */

public class Piece
{
    public final int value;
    private ImageIcon icon;
    private Set<Att> atts = new LinkedHashSet<>();

    public Piece(byte[] binaryVal)
    {
        if(binaryVal == null) value = -1;
        else
        {
            int val = 0;
            for (int i = 0; i < binaryVal.length; ++i)
                val = (val << 1) + (binaryVal[i]);
            value = val;
        }
    }

    public Piece(List<Attribute> gameAtts, byte[] attVals)
    {
        this(attVals);
        for(int i = 0; i < gameAtts.size(); i++)
            addAtt(gameAtts.get(i), attVals[i]);
    }

    /**
     * Adds an attribute to the Piece. The attNum specifies which attribute value to take.
     * @param newAttr Attribute
     * @param attNum byte
     */
    public void addAtt(Attribute newAttr, byte attNum)
    {
        if(newAttr != null && (attNum == 0 || attNum == 1) &&
           !(atts.contains(newAttr.att0) || atts.contains(newAttr.att1)))
                atts.add(newAttr.getAtt(attNum));
    }

    /**
     * Checks if the Piece has a specific attribute.
     * @param attr Att
     * @return boolean
     */
    public boolean hasAtt(Att attr)
    {
        return atts.contains(attr);
    }

    /**
     * Checks if the piece has the attributes specified by the list.
     * @param attrs List<Att>
     * @return boolean
     */
    public boolean hasAtts(Att[] attrs)
    {
        if(attrs == null || attrs.length == 0) return false;
        boolean matches = true;
        for(Att a : attrs)  matches &= atts.contains(a);
        return matches;
    }

    /**
     * Returns the attribute set of the Piece.
     * @return HashSet<Att>
     */
    public Set<Att> getAtts()
    {
        return atts;
    }

    /**
     * Checks if a Piece is similar to another Piece.
     * @param other Piece
     * @return boolean
     */
    public boolean isSimilar(Piece other)
    {
        if(other == null) return false;
        if(atts.isEmpty() && other.atts.isEmpty()) return true;
        for(Att a : atts)
            if(other.atts.contains(a))
                return true;
        return false;
    }

    /**
     * Returns the number of shared attribute between two pieces. Returns negative -1 if piece is null.
     * @param other Piece
     * @return int
     */
    public int countShared(Piece other)
    {
        if(other == null) return -1;
        int count = 0;
        Set<Att> otherAtts = other.atts;
        for(Att a : atts)
            if(otherAtts.contains(a)) count++;
        return count;
    }

    /**
     * Initializes the icon of the piece. Should be called after you have added all the attributes you want.
     */
    public void initIcon()
    {
        icon = PieceIcons.getIcon(atts);
    }

    /**
     * Returns the ImageIcon representing the piece.
     * @return ImageIcon
     */
    public ImageIcon getIcon()
    {
        return icon;
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        for(Att a : atts)
            sb.append(a.toString());
        return sb.toString();
    }
}
