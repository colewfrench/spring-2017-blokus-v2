package edu.up.cs301.blokus;

import android.content.Context;
import android.widget.ImageButton;

/**
 * Created by frenchco19 on 3/25/2017.
 */

public class PieceDisplayButton extends BoardButton {

    private boolean isActive;

    public PieceDisplayButton(Context context, int initRow, int initCol,
                              boolean initSelected, boolean initActive) {
        super(context, initRow, initCol, initSelected);

        this.isActive = initActive;
    }

    public boolean isActive()
    {
        return this.isActive;
    }

    public void setActive(boolean s)
    {
        this.isActive = s;
    }
}
