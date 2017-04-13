package edu.up.cs301.blokus;

import android.content.Context;
import android.widget.ImageButton;

/**
 * Created by frenchco19 on 3/25/2017.
 */

public class PieceDisplayButton extends ImageButton {

    private boolean isActive;
    private boolean isSelected;
    private int row, col;

    public PieceDisplayButton(Context context, int initRow, int initCol, boolean initActive) {
        super(context);

        this.row = initRow;
        this.col = initCol;
        this.isActive = initActive;
        this.isSelected = false;
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
        return this.isSelected;
    }

    public void setSelected(boolean s)
    {
        this.isSelected = s;
    }
}
