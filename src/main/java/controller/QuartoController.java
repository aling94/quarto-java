package controller;

import model.Attribute;
import model.Game.Game;
import model.Game.GameAI.*;
import model.Game.Move;
import model.Game.Quarto;
import model.Piece;
import view.QuartoView;
import view.SquareButton;

import javax.swing.*;
import java.awt.*;

public class QuartoController
{

    private Game game;
    private QuartoView view;

    private boolean gameInSession = false;

    private int[] clickedSqr;
    private int clickedPce = -1;
    private GameAI[] cpus = new GameAI[2];
    private GameAI assister = new NormalAI();
    private Move assist;

    public static void main(String[] args)
    {
        QuartoController qc = new QuartoController();
    }

    /**
     * Initializes all the ActionListeners needed by the ChessGUI to run as a game.
     */
    public QuartoController()
    {
        view = new QuartoView();
        initCPUs(0);
        initCPUs(1);
        // Set the default listener for the squares and pieces
        view.addSquareListerner(e -> alertNoGame());
        view.addPieceListerner( e -> alertNoGame());
        // And the listeners for the buttons
        view.addNewGameListener(e -> newGame());
        view.addForfeitListener(e -> actionGuard(this::forfeit));
        view.addUndoListener(   e -> actionGuard(this::undoMove));
        view.addAssistListener( e -> actionGuard(this::assistMove));
        view.addRunTurnListener(e -> actionGuard(this::runTurn));
        // Lambda functions are cool
    }

    /* =========================================== ACTION RESPONSES ================================================= */

    /**
     * Checks if the game is in session before running the response function. To be used for the forfeit, undo, and
     * run turn buttons.
     * @param actionResponse Runnable
     */
    private void actionGuard(Runnable actionResponse)
    {
        if(!gameInSession) alertNoGame();
        else actionResponse.run();
    }

    /**
     * Called when user presses the New Game button. Prompts the user for the desired Game mode and
     * loads in the chosen setup. If a game is already in session, will ask for confirmation.
     */
    private void newGame()
    {
        if(!gameInSession || (confirmNewGame(0) && confirmNewGame(1)))
        {
            Game mode = promptGameMode();
            if(mode != null) initGame(mode);
        }
    }

    /**
     * Called when the Forfeit Game button is pressed. Alerts the users that the current moving player
     * has forfeited. Then increments the score of the "Winner" and ends the current game session.
     */
    private void forfeit()
    {
        byte turn = game.getTurn();
        view.incrementScore(turn ^ 1);
        displayMsg(view.getPlayerName(turn) + " forfeits!", "Forfeit!");
        gameInSession = false;
    }

    /**
     * Called when the End Turn button is clicked. Ends the turn with the chosen piece and square selected.
     */
    private void runTurn()
    {
        boolean firstMove = game.getNextPick() == null  && clickedPce != -1,
                lastMove  = clickedSqr != null && game.isLastTurn(),
                normMove  = clickedSqr != null && clickedPce != -1;

        if(game.isCPUTurn())
        {
            game.runTurn(-1,-1,null);
            Move move = game.prevMove();
            renderTurn(new int[]{move.x, move.y}, move.placed, move.picked);

        } else if(firstMove || lastMove || normMove)
        {
            int x = -1, y = -1;
            if(!firstMove)
            {
                x = clickedSqr[0];
                y = clickedSqr[1];
            }
            Piece lastPick = game.getNextPick();
            Piece nextPick = lastMove? null : game.getFrees().get(clickedPce);

            game.runTurn(x,y, nextPick);
            renderTurn(clickedSqr, lastPick, nextPick);
        }
    }

    /**
     * Called when the undo button is clicked. Will undo the move in the internal Game and update the view.
     * Does nothing if there are no moves to undo.
     */
    private void undoMove()
    {
        Move mv = game.undoTurn(false);
        if (mv == null)
        {
            displayMsg("No moves to undo.", "Nothing to undo");
            return;
        }
        view.rmvPiece(new int[] {mv.x, mv.y});
        renderTurn(null, null, game.getNextPick());
    }

    private void assistMove()
    {
        if(game.isCPUTurn()) return;
        resetClicks();
        assist = (assist == null)? assister.genMove(game) : assist;
        clickedSqr = new int[]{assist.x, assist.y};
        clickedPce = game.getFrees().indexOf(assist.picked);
        view.highlightSquare(clickedSqr);
        view.highlightPiece(clickedPce);
    }

    /**
     * Helper function for initializing the ActionListener of the SquareButtons in the GUI. The ActionListener retrieves
     * the Square that this button represents and calls highlightMoves or attemptMoves depending on whether it was the
     * first or second click.
     */
    private void initSquareListeners()
    {
        view.addSquareListerner(e ->
        {
            if (!gameInSession) alertNoGame();
            else
            {
                SquareButton clicked = ((SquareButton) e.getSource());
                if(game.isOpen(clicked.x, clicked.y))
                {
                    view.unHighlightSquare(clickedSqr);
                    clickedSqr = new int[]{clicked.x, clicked.y};
                    view.highlightSquare(clickedSqr);
                }
            }
        });
    }

    /**
     * Helper function for initializing the ActionListener of the JButtons for Pieces.
     */
    private void initPieceListeners()
    {
        view.addPieceListerner(e ->
        {
            if (!gameInSession) alertNoGame();
            else
            {
                int pieceI = ((SquareButton) e.getSource()).x;
                if(pieceI < game.getFrees().size())
                {
                    view.unHighlightPiece(clickedPce);
                    view.highlightPiece(pieceI);
                    clickedPce = pieceI;
                }
            }
        });
    }


    /* ==================================================== HELPERS ================================================= */

    /**
     * Helper for initializing a new game.
     * @param mode Game
     */
    private void initGame(Game mode)
    {
        game = mode;
        game.setAI(cpus[0], 0);
        game.setAI(cpus[1], 1);
        view.setupGame(game);  // This inits a brand new board with new SquareButtons for the game
        gameInSession = true;
        clickedSqr = null;
        assist = null;
        clickedPce = -1;
        setTurnColor();
        initPieceListeners(); // So we need to re-initialize the action listeners
        initSquareListeners();
    }

    /**
     * Helper function to re-render the view after a turn has successfully run. Will also reset the highlighted squares
     * and any available assist moves.
     * @param sqr int[]
     * @param last Piece
     * @param next Piece
     */
    private void renderTurn(int[] sqr, Piece last, Piece next)
    {
        view.setPiece(sqr, last);
        view.setNextPick(next);
        view.renderPieces(game);
        setTurnColor();
        resetClicks();
        assist = null;
        validateGameState();
    }

    /**
     * Checks if the game has ended and prints out the appropriate message if so.
     */
    private void validateGameState()
    {
        int state = game.winner();
        if(state == -1) return;
        gameInSession = false;
        if(state != -2)
        {
            displayMsg(view.getPlayerName(state) + " wins!", "WINNER!");
            view.incrementScore(state);
        } else displayMsg("It's a draw!", "DRAW!");
    }

    /**
     * Resets the clicked squares and pieces on the view and resets the members holding the last clicked buttons.
     */
    private void resetClicks()
    {
        view.unHighlightSquare(clickedSqr);
        view.unHighlightPiece(clickedPce);
        clickedSqr = null;
        clickedPce = -1;
    }

    /**
     * Swaps the highlighted player name depending on the current turn.
     */
    private void setTurnColor()
    {
        if(game.getTurn() == 0) view.setNameColors(Color.BLUE, Color.BLACK);
        else view.setNameColors(Color.BLACK, Color.BLUE);
    }

    /**
     * Prompts the user to select a Game mode.
     * @return Game
     */
    private Game promptGameMode()
    {
        Game[] modes = {new Quarto(), new Quarto(Attribute.BAR), new Quarto(Attribute.SLASH)};
        return (Game) JOptionPane.showInputDialog(null, "Choose the Game mode:", "Game mode selection",
                JOptionPane.QUESTION_MESSAGE, null, modes, modes[0]);
    }

    /**
     * Displays a JOptionPane with the message and title.
     * @param msg String
     * @param windowTitle String
     */
    private void displayMsg(String msg, String windowTitle)
    {
        JOptionPane.showMessageDialog (null, msg, windowTitle, JOptionPane.PLAIN_MESSAGE);
    }

    /**
     * Displays the message telling the players to start a new game.
     */
    private void alertNoGame()
    {
        JOptionPane.showMessageDialog (null, "Please start a new game.", "No game in session.", JOptionPane.PLAIN_MESSAGE);
    }

    /**
     * Displays a confirmation window asking the player specified by number if they want to restart the game.
     * Returns true if they answered Yes.
     * @param playerNum int
     * @return boolean
     */
    private boolean confirmNewGame(int playerNum)
    {
        if(cpus[playerNum] != null) return true;
        String playerName = view.getPlayerName(playerNum);
        int reply = JOptionPane.showConfirmDialog(null, "Does " + playerName + " want to restart?", "New Game?",
                JOptionPane.YES_NO_OPTION);
        return reply == JOptionPane.YES_OPTION;
    }


    /**
     * Helper for initializing the CPUs for the game.
     * @param playerNum int
     */
    private void initCPUs(int playerNum)
    {
        if(!view.getPlayerName(playerNum).equals("CPU" + playerNum)) return;
        GameAI[] modes = {new FirstAI(), new RandomAI(), new NormalAI(), new HardAI()};
        while(cpus[playerNum] == null)
            cpus[playerNum] =(GameAI) JOptionPane.showInputDialog(null, "Choose the CPU level for Player" + playerNum,
                    "CPU difficulty selection", JOptionPane.QUESTION_MESSAGE, null, modes, modes[0]);
    }
}
