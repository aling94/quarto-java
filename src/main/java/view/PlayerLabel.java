package view;

import javax.swing.*;
import java.awt.*;

public class PlayerLabel extends JLabel
{
    private String name;
    private int score = 0;

    /**
     * Constructors a new player that plays the given piece color. Will prompt the user to input a name for this PlayerLabel.
     * @param playerNum
     */
    public PlayerLabel(byte playerNum)
    {
        super("", SwingConstants.CENTER);
        setFont( new Font(getFont().getFontName(), Font.PLAIN, 20) );
        setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        name = promptName(playerNum);
        updateScoreLabel();
    }

    /**
     * Increments the score of the player and updates the JLabel associated with this PlayerLabel.
     */
    public void incrementScore()
    {
        score++;
        updateScoreLabel();
    }

    /**
     * Helper function to update the text of the internal JLabel with the new score.
     */
    private void updateScoreLabel()
    {
        setText(name + " : " + score);
    }

    public String getName()
    {
        return name;
    }

    public void setName(String newName)
    {
        name = newName;
    }

    /**
     *  Helper function for prompting user input for a name for the player. The color the player will be playing as
     *  is inputted as a string.
     * @param num byte
     * @return String
     */
    private String promptName(byte num)
    {
        String player = (num == 0)? "Player0" : "Player1";
        String msg = String.format("Please enter the name of %s.\nLeave it blank or type CPU%d to set as CPU.", player, num);
        String name = JOptionPane.showInputDialog(msg);
        if(name == null || name.isEmpty()) name = "CPU" + num;
        name = name.trim().replaceAll("\\s+", " ");             // Collapse spaces to one space
        name = name.substring(0, Math.min(name.length(), 12));  // Truncate to length 12
        return name.trim();                                     // Trim it again
    }
}
