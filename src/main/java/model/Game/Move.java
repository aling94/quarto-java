package model.Game;

import model.Piece;

/**
 * Represents a move in a normal game cycle of Quarto
 */
public class Move
{

    public final int x;
    public final int y;
    public final Piece placed;
    public final Piece picked;

    /**
     * Default constructor for move. Pass it the coordinates of the placed piece, the placed piece, and the
     * piece picked for the next player
     * @param place Piece
     * @param mvX int
     * @param mvY int
     * @param pick Piece
     */
    public Move(Piece place, int mvX, int mvY, Piece pick)
    {
        placed = place;
        x = mvX;
        y = mvY;
        picked = pick;
    }
}
