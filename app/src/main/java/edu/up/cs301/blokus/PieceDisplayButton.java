package edu.up.cs301.blokus;

import android.content.Context;

/**
 * PieceDisplayButton class
 * used to create 'preview window' for gameplay purposes
 *
 * @author Adrian Low
 * @author Cole French
 * @author Devin Ajimine
 * @author Evan Sterba
 */

public class PieceDisplayButton extends BoardButton
{
    /**
     * constructor
     *
     * @param context context
     * @param initRow row of button
     * @param initCol column of button
     * @param initSelected if the button is selected or not
     *
     */
    public PieceDisplayButton(Context context, int initRow, int initCol,
                              boolean initSelected)
    {
        super(context, initRow, initCol, initSelected);
    }//ctor
}
