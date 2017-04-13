package edu.up.cs301.blokus;

import android.content.Context;
import android.widget.ImageButton;

import java.io.Serializable;

/**
 * Class for BoardButtons
 * Image buttons but with row, column, and color tracking
 *
 * @author Adrian Low
 * @author Devin Ajimine
 */
public class BoardButton extends ImageButton {

    private int color;

    private boolean boardClicked;

    private int row, col;

    public BoardButton(Context context, int initRow, int initCol, boolean initBoardClicked) {
        super(context);

        this.row = initRow;
        this.col = initCol;
        this.boardClicked = initBoardClicked;
    }

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

    public boolean isBoardClicked()
    {
        return boardClicked;
    }

    public void setBoardClicked(boolean t)
    {
        this.boardClicked = t;
    }
}
