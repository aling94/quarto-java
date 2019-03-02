package view;

import model.Game.Game;
import model.Game.Quarto;
import model.Piece;

import java.awt.event.ActionListener;
import java.util.List;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.*;
import java.awt.*;
import javax.swing.border.*;

/**
 * View for the Game
 */
public class QuartoView
{

    private JFrame window = new JFrame("Quarto App");
    private JPanel parentPanel, content;
    private SquareButton[][] board;
    private SquareButton[] pieces;
    private SquareButton nextPiece;

    private JButton newgameButton, forfeitButton, undoButton, runTurnButton, assistButton;

    private PlayerLabel player0;
    private PlayerLabel player1;

    /**
     * Initializes all the components of the GUI and puts them into a JFrame to display.
     */
    public QuartoView()
    {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
        catch (Exception ignored) { }

        parentPanel = new JPanel(new BorderLayout(3,3));
        parentPanel.setBorder(new EmptyBorder(0,0,0,0));
        content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        // Setup an empty board at the start
        // Add all the components to the parent panel
        parentPanel.add(initToolbar(), BorderLayout.PAGE_START);
        parentPanel.add(initScoreBar(), BorderLayout.PAGE_END);
        parentPanel.add(content, BorderLayout.CENTER);
        setupGame(new Quarto());

        // Pack and fix the window dim. Set sizing and location options.
        window.add(parentPanel);
        window.pack();
        window.setMinimumSize(window.getSize());
        window.setResizable(false);
        window.setVisible(true);
        window.setLocationRelativeTo(null);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    /**
     * Given a Game object, the GUI will clear the current board and render a new board matching the one contained in
     * the Game object.
     * @param game Game
     */
    public void setupGame(Game game)
    {
        if(game == null) return;
        content.removeAll();
        content.revalidate();
        setupBoard(game);
        content.add(Box.createRigidArea(new Dimension(0,10)));
        setupPieces(game);
        window.pack();
    }

    /**
     * Renders the currently free pieces onto the roster grid.
     * @param game Game
     */
    public void renderPieces(Game game)
    {
        List<Piece> frees = game.getFrees();
        for(int i = 0; i < pieces.length; i++)
        {
            ImageIcon icon = (i >= frees.size())? null : frees.get(i).getIcon();
            pieces[i].setIcon(icon);
        }
    }

    /**
     * Returns the name of the PlayerLabel playing the given color.
     * @param player int
     * @return String
     */
    public String getPlayerName(int player)
    {
        if(player == 0)      return player0.getName();
        else if(player == 1) return player1.getName();
        else return "";
    }

    /**
     * Sets the font color of the display names.
     * @param p0Clr Color
     * @param p1Clr Color
     */
    public void setNameColors(Color p0Clr, Color p1Clr)
    {
        player0.setForeground(p0Clr);
        player1.setForeground(p1Clr);
    }

    /**
     * Increments the score of the player represented by the number.
     * @param player int
     */
    public void incrementScore(int player)
    {
        if(player == 0)      player0.incrementScore();
        else if(player == 1) player1.incrementScore();
    }

    /**
     * Displays the piece on the board if the cooardinates are valid.
     * @param coords int[]
     * @param piece Piece
     */
    public void setPiece(int[] coords, Piece piece)
    {
        if(isValidSquare(coords) && piece != null)
            board[coords[0]][coords[1]].setIcon(piece.getIcon());
    }

    /**
     * Removes a piece from the board view.
     * @param coords int[]
     */
    public void rmvPiece(int[] coords)
    {
        if(isValidSquare(coords))
            board[coords[0]][coords[1]].setIcon(null);
    }

    /**
     * Displays the piece in the nextPick box.
     * @param piece Piece
     */
    public void setNextPick(Piece piece)
    {
        ImageIcon icon = (piece == null)? null : piece.getIcon();
        nextPiece.setIcon(icon);
    }

    /**
     * Helper function for determining if the coordinates refer to a valid square on the board.
     * @param coords int[]
     * @return boolean
     */
    public boolean isValidSquare(int[] coords)
    {
        if(board == null || coords == null || coords.length < 2) return false;
        int x = coords[0], y = coords[1];
        return (x >= 0 && y >= 0) && (x < board.length && y < board[0].length);
    }

    /**
     * Helper function for determining if the coordinates refer to a valid square on the board.
     * @param i int
     * @return boolean
     */
    public boolean isValidPiece(int i)
    {
        return pieces != null && (i >= 0) && (i < pieces.length);
    }

    /**
     * Highlights the given square if given valid coordinates.
     * @param coords int[]
     */
    public void highlightSquare(int[] coords)
    {
        if(isValidSquare(coords))
            board[coords[0]][coords[1]].highLight();
    }

    /**
     * Restores the square at the given coordinates to its default color.
     * @param coords int[]
     */
    public void unHighlightSquare(int[] coords)
    {
        if(isValidSquare(coords))
            board[coords[0]][coords[1]].unHighLight();
    }

    /**
     * Highlights the square of the piece if given valid indices.
     * @param pieceI int
     */
    public void highlightPiece(int pieceI)
    {
        if(isValidPiece(pieceI)) pieces[pieceI].highLight();
    }

    /**
     * Restores the square of the piece at the given index to its default color.
     * @param pieceI int
     */
    public void unHighlightPiece(int pieceI)
    {
        if(isValidPiece(pieceI)) pieces[pieceI].unHighLight();
    }


    /* ================================================ GUI SETUP =================================================== */

    /**
     * Helper function for initializing the toolbar component. Contains the undo, forfeit, and new game buttons.
     * @return JToolBar
     */
    private JToolBar initToolbar()
    {
        JToolBar tools = new JToolBar();
        tools.setFloatable(false);
        newgameButton = new JButton("New Game");
        forfeitButton = new JButton("Forfeit Game");
        undoButton = new JButton("Undo Move");
        assistButton = new JButton("Assist");
        runTurnButton = new JButton("End Turn");
        tools.add(newgameButton); tools.addSeparator();
        tools.add(forfeitButton); tools.addSeparator();
        tools.add(undoButton); tools.addSeparator();
        tools.add(assistButton); tools.addSeparator();
        tools.add(runTurnButton);
        return tools;
    }

    /**
     * Initializes the score bar component.
     * @return JPanel
     */
    private JPanel initScoreBar()
    {
        player0 = new PlayerLabel((byte)0);
        player1 = new PlayerLabel((byte)1);
        JPanel scores = new JPanel(new GridLayout(0,2));
        scores.add(player0);
        scores.add(player1);
        return scores;
    }

    /**
     * Clears the board and sets up new JButtons to represent the board.
     * @param game Game
     */
    private void setupBoard(Game game)
    {
        int dim = game.dim(), d = (60+dim-1) * dim;
        board = new SquareButton[dim][dim];

        JPanel grid = new JPanel(new GridLayout(0, dim, 1, 1));
        grid.setBackground(Color.BLACK);

        for(int y = dim-1; y >= 0; y--)
            for(int x = 0; x < dim; x++)
            {
                board[x][y] = new SquareButton(x,y, null);
                grid.add(this.board[x][y]);
            }
        grid.setMaximumSize(new Dimension(d, d));
        content.add(grid);
    }

    /**
     * Initializes all the pieces in the game on the roster grid.
     * @param game Game
     */
    private void setupPieces(Game game)
    {
        List<Piece> frees = game.getFrees();
        int w = frees.size() / 2;
        JPanel grid = new JPanel(new GridLayout(0, w, 1, 1));
        grid.setBackground(Color.BLACK);

        pieces = new SquareButton[frees.size()];
        for(int i = 0; i < pieces.length; i++)
        {
            pieces[i] = new SquareButton(i, 0, Color.WHITE);
            grid.add(pieces[i]);
        }
        renderPieces(game);

        nextPiece = new SquareButton(-1,1, Color.WHITE);
        JPanel np = new JPanel(new GridLayout(1,1));
        np.setPreferredSize(new Dimension(80,80));
        np.setMaximumSize(new Dimension(80,80));
        np.add(nextPiece);

        // Wrap the pieces and the nextPick button together and add it to the content panel
        JPanel pieceWrapper = new JPanel();
        pieceWrapper.setLayout(new BoxLayout(pieceWrapper, BoxLayout.X_AXIS));
        pieceWrapper.add(np);
        pieceWrapper.add(Box.createRigidArea(new Dimension(5,0)));
        pieceWrapper.add(grid);
        content.add(pieceWrapper);
    }

    /* ============================================= ACTION LISTENERS =============================================== */

    /**
     * Adds an ActionListener for the New Game button.
     * @param actl ActionListener
     */
    public void addNewGameListener(ActionListener actl)
    {
        newgameButton.addActionListener(actl);
    }

    /**
     * Adds an ActionListener for the Forfeit Game button.
     * @param actl ActionListener
     */
    public void addForfeitListener(ActionListener actl)
    {
        forfeitButton.addActionListener(actl);
    }

    /**
     * Adds an ActionListener for the Undo Move button.
     * @param actl ActionListener
     */
    public void addUndoListener(ActionListener actl)
    {
        undoButton.addActionListener(actl);
    }

    /**
     * Adds an ActionListener for the Run Turn button.
     * @param actl ActionListener
     */
    public void addRunTurnListener(ActionListener actl)
    {
        runTurnButton.addActionListener(actl);
    }

    /**
     * Adds an ActionListener for the Assist button.
     * @param actl ActionListener
     */
    public void addAssistListener(ActionListener actl)
    {
        assistButton.addActionListener(actl);
    }

    /**
     * Adds an ActionListener for all the JButtons representing the board squares.
     * @param actl ActionListener
     */
    public void addSquareListerner(ActionListener actl)
    {
        for (SquareButton[] col : board)
            for (SquareButton sb : col)
                sb.addActionListener(actl);
    }

    /**
     * Adds an ActionListener for all the JButtons representing the board squares.
     * @param actl ActionListener
     */
    public void addPieceListerner(ActionListener actl)
    {
        for (SquareButton piece : pieces) piece.addActionListener(actl);
    }

}
