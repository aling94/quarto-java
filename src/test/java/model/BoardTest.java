package model;

import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;

import static org.junit.Assert.*;


public class BoardTest
{

    private Board board;
    private int xLen, yLen;

    private Piece[][] getInnerBoard(Board b) throws NoSuchFieldException, IllegalAccessException
    {
        Class<?> bClass = Board.class;
        Field field = bClass.getDeclaredField("board");
        field.setAccessible(true);
        return (Piece[][]) field.get(b);
    }

    /**
     * Initializes a board with a (0,0).
     */
    @Before
    public void setup()
    {
        xLen = yLen = 4;
        board = new Board(xLen, yLen);
        board.put(new Piece(null), 0, 0);
    }

    /**
     * Tests that the custom dimension constructor creates a 2D of the correct dimensions.
     */
    @Test
    public void testCustomBoardConstructor() throws IllegalAccessException, NoSuchFieldException
    {
        Board b = new Board(5,6);
        assertEquals(5, b.xLength);
        assertEquals(6, b.yLength);
        Piece[][] internalBoard = getInnerBoard(b);
        assertEquals(5, internalBoard.length);
        assertEquals(6, internalBoard[0].length);
    }

    /**
     * Tests that the custom dimension constructor with invalid dimensions. Should default to 8x8 board
     */
    @Test
    public void testInvalidCustomBoard() throws IllegalAccessException, NoSuchFieldException
    {
        Board b = new Board(-1,6);
        assertEquals(xLen, b.xLength);
        assertEquals(yLen, b.yLength);
        Piece[][] internalBoard = getInnerBoard(b);
        assertEquals(xLen, internalBoard.length);
        assertEquals(yLen, internalBoard[0].length);
    }

    /**
     * Tests that all squares actually on the board are valid.
     */
    @Test
    public void testIsValidSquare()
    {
        for(int y = 0; y < yLen; y++)
            for(int x = 0; x < xLen; x++)
                assertTrue(board.isValid(x, y));
    }

    /**
     * Tests the invalid squares are correctly reported.
     */
    @Test
    public void testInvalidSquare()
    {
        assertFalse(board.isValid(-1, -1));
        assertFalse(board.isValid(4, 4));
    }

    /**
     * Tests placing and removing pieces from the board.
     */
    @Test
    public void testSetPieceValid()
    {
        assertTrue(board.hasPiece(0,0));
        board.remove(0, 0);
        assertFalse(board.hasPiece(0,0));
    }

    /**
     * Tests attempts to place piece in invalid squares.
     */
    @Test
    public void testSetPieceInValid()
    {
        assertFalse(board.hasPiece(-1, -1));
        board.put(new Piece(null), -1, -1);
        assertFalse(board.hasPiece(-1, -1));
    }
}