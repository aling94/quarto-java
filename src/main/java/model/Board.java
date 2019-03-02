package model;

/**
 * Representation of a rectangular game board as a piece array.
 */

public class Board
{
    public final int xLength;
    public final int yLength;

    private Piece[][] board;

    /**
     * Constructors a rectangular board of dimensions xLen by yLen. Both dimensions must be positive. Defaults to
     * 4x4 board if not.
     * @param xLen int
     * @param yLen int
     */
    public Board(int xLen, int yLen)
    {
        if(xLen <= 0 || yLen <= 0)
            xLen = yLen = 4;
        board = new Piece[xLen][yLen];
        xLength = xLen;
        yLength = yLen;
    }

    /**
     * Returns true if coordinates refer to valid square on the board.
     * @param x int
     * @param y int
     * @return boolean
     */
    public boolean isValid(int x, int y)
    {
        return (x >= 0) && (x < xLength) && (y >= 0) && (y < yLength);
    }

    /**
     * Returns the piece at the coordinates. Null if no piece exists or invalid coordinates.
     * @param x int
     * @param y int
     * @return Piece
     */
    public Piece get(int x, int y)
    {
        if(isValid(x,y)) return board[x][y];
        return null;
    }

    /**
     * Places a piece onto the board. Does nothing if invalid coordinates.
     * @param x int
     * @param y int
     */
    public void put(Piece piece, int x, int y)
    {
        if(isValid(x, y))
            board[x][y] = piece;
    }

    /**
     * Removes a piece onto the board. Does nothing if invalid coordinates.
     * @param x int
     * @param y int
     */
    public void remove(int x, int y)
    {
        if(isValid(x, y)) board[x][y] = null;
    }

    /**
     * Returns true if the board has a piece on the specified coordinates. False if empty or invalid coordinates.
     * @param x int
     * @param y int
     * @return boolean
     */
    public boolean hasPiece(int x, int y)
    {
        return isValid(x, y) && board[x][y] != null;
    }

    /**
     * Returns true if square is empty. False if occupied or invalid coordinates.
     * @param x int
     * @param y int
     * @return boolean
     */
    public boolean isOpen(int x, int y)
    {
        return isValid(x, y) && board[x][y] == null;
    }
}
