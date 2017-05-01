package edu.up.cs301.blokus;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageButton;

/**
 *  PieceButton class
 *  Image Buttons but with highlight tracking.
 *  used to select pieces to view on the preview screen and play
 *
 * @author Cole French
 * @author Evan Sterba
 */
public class PieceButton extends ImageButton {

    private boolean isHighlighted;
    private int buttonPiece;

    public PieceButton(Context context)
    {
        super(context);
        this.isHighlighted = false;
        buttonPiece = -1;
    }

    public PieceButton(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public PieceButton(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }
    //getters and setters
    public void setIsHighlighted(boolean h)
    {
        this.isHighlighted = h;
    }

    public boolean isHighlighted()
    {
        return this.isHighlighted;
    }

    public int getButtonPiece()
    {
        return this.buttonPiece;
    }

    public void setButtonPiece(int pieceID)
    {
        this.buttonPiece = pieceID;
    }
}
