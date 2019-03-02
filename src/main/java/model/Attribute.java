package model;

import static model.Attribute.Att.*;

/**
 * Represents an attribute a piece can have. An attribute has a name and two possible values.
 */

public class Attribute
{

    public enum Att {BWN, YLW, BIG, SML, SQR, CIR, HLW, SLD, FORW, BACK, VERT, DASH}

    public final String name;
    public final Att att0;
    public final Att att1;

    /**
     * Constructs an attribute object with the given name and its two values.
     * @param attName String
     * @param a0 Att
     * @param a1 Att
     */
    public Attribute(String attName, Att a0, Att a1)
    {
        name = attName;
        att0 = a0;
        att1 = a1;
    }

    public boolean isValid()
    {
        return att0 != att1;
    }

    /**
     * Returns the attribute specified by the given attribute number, 0 or 1. Returns null if invalid attribute number.
     * @param attNum byte
     * @return Att
     */
    public Att getAtt(byte attNum)
    {
        if (attNum == 0) return att0;
        if (attNum == 1) return att1;
        return null;
    }

    @Override
    public int hashCode()
    {
        return 42;
    }

    @Override
    public boolean equals(Object other)
    {
        if(other == null || !(other instanceof Attribute))
            return false;

        Attribute rhs = (Attribute)other;

        return (att0 == rhs.att0 && att1 == rhs.att1) ||
                (att1 == rhs.att0 && att0 == rhs.att1);
    }

    /**
     * Pre-defined Attributes to be used in games.
     */
    public static final Attribute COLOR = new Attribute("color", BWN, YLW);
    public static final Attribute SIZE = new Attribute("size", BIG, SML);
    public static final Attribute SHAPE = new Attribute("shape", SQR, CIR);
    public static final Attribute TOP = new Attribute("top", HLW, SLD);
    public static final Attribute SLASH = new Attribute("slash", FORW, BACK);
    public static final Attribute BAR = new Attribute("bar", VERT, DASH);
}

