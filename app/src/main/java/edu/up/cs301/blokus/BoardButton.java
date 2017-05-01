package edu.up.cs301.blokus;

import android.content.Context;
import android.widget.ImageButton;

/**
 * Class for BoardButtons
 * Image buttons but with row, column, and color tracking
 * used to create the game board
 *
 * @author Adrian Low
 * @author Devin Ajimine
 */
public class BoardButton extends ImageButton {

    private int color;
    private boolean isSelected;
    private int row, col;

    /**
     * constructor
     *
     * @param context context
     * @param initRow row of button
     * @param initCol column of button
     * @param initSelected if the BoardButton is selected or not
     *
     */
    public BoardButton(Context context, int initRow, int initCol, boolean initSelected) {
        super(context);

        this.row = initRow;
        this.col = initCol;
        this.isSelected = initSelected;
    }//ctor

    //getters and setters
    public void setColor(int color)
    {
        this.color = color;
    }

    public int getColor()
    {
        return this.color;
    }

    public int getRow()
    {
        return row;
    }

    public int getCol()
    {
        return col;
    }

    public boolean isSelected()
    {
        return isSelected;
    }

    public void setSelected(boolean t)
    {
        this.isSelected = t;
    }
}
