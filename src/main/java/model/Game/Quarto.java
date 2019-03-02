package model.Game;

import model.Attribute;
import model.Board;

public class Quarto extends Game
{

    /**
     * Default constructor. Initializes the board and the pieces.
     */
    public Quarto()
    {
        addDefaultAttrs();
        board = new Board(4,4);
        setupPieces();
    }

    /**
     * Constructor with the option of supplying an extra attribute to play with.
     * @param newAttr Attribute
     */
    public Quarto(Attribute newAttr)
    {
        addDefaultAttrs();
        addAttr(newAttr);
        board = new Board(atts.size(), atts.size());
        setupPieces();
    }

    /**
     * Adds an extra attribute to the game. Rejects the attribute if it shares any values with the existing attributes.
     * @param newAttr Attribute
     */
    private void addAttr(Attribute newAttr)
    {
        boolean disjoint = true;
        for(Attribute a : atts) disjoint &= (a != newAttr);
        if(disjoint) atts.add(newAttr);
    }

    /**
     * Adds the default Quarto attributes to the game.
     */
    private void addDefaultAttrs()
    {
        atts.add(Attribute.SIZE);
        atts.add(Attribute.SHAPE);
        atts.add(Attribute.COLOR);
        atts.add(Attribute.TOP);
    }

    @Override
    public String toString()
    {
        if(atts.size() == 4) return "Standard";
        return "Custom: " + atts.get(4).name;
    }
}
