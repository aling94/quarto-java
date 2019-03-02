package view;

import javax.swing.*;
import java.awt.*;

public class SquareButton extends JButton
{
    public final int x;
    public final int y;
    Color defaultClr;

    /**
     * Constructs a new JButton that also stores a Square representing its location on a board
     * @param x int
     * @param y int
     * @param color
     */
    public SquareButton(int x, int y, Color color)
    {
        super();
        this.x = x;
        this.y = y;
        defaultClr = (color == null)? new Color(148, 93, 53) : color;

        setPreferredSize(new Dimension(60,60));
        setBackground(defaultClr);
        setContentAreaFilled(false);
        setOpaque(true);
        setMargin(new Insets(0,0,0,0));
        setFocusPainted(false);
    }

    /**
     * Sets the background color to green to represent a highlighted square.
     */
    public void highLight()
    {
        setBackground(Color.GREEN);
    }

    /**
     * Restores the original color of this SquareButton.
     */
    public void unHighLight()
    {
        setBackground(defaultClr);
    }
}
