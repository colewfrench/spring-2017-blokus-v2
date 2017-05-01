package edu.up.cs301.blokus.pieces;

import edu.up.cs301.blokus.BlokusGameState;
import edu.up.cs301.blokus.PieceBlok;

/**
 * Piece Definition
 *
 * @author Adrian Low
 */
public class PieceT4 extends PieceTemplate
{
    public PieceT4()
    {
        super(PIECE_T4);

        PieceBlok[] myShape = new PieceBlok[4];

        for (int i = 0; i < 4; i++)
        {
            myShape[i] = new PieceBlok(0,0, BlokusGameState.EMPTY_BLOK, i);

            //Set Adjacencies for each blok using getAdjArray method
            myShape[i].setAdjacencies(getAdjArray(i));
            myShape[i].setHasCorner(isValidCorner(i));
        }

        setPieceShape(myShape);
        this.setAnchor(myShape[2]);

        super.rotate();
        super.rotate();
    }

    @Override
    protected boolean isValidCorner(int pieceBlokId)
    {
        switch (pieceBlokId)
        {
            case 2:
                return false;
            default:
                return true;
        }
    }

    @Override
    protected int[] getAdjArray(int pieceBlokId) {
        int[] temp = new int[4];
        int top = NO_ADJ;
        int right = NO_ADJ;
        int bottom = NO_ADJ;
        int left = NO_ADJ;

        //Switch sets adjacencies for each blok using array
        switch (pieceBlokId) {
            case 0:
                top = 2;
                break;
            case 1:
                left = 2;
                break;
            case 2:
                bottom = 0;
                left = 3;
                right = 1;
                break;
            case 3:
                right = 2;
                break;
        }

        temp[PieceBlok.LEFT] = left;
        temp[PieceBlok.TOP] = top;
        temp[PieceBlok.RIGHT] = right;
        temp[PieceBlok.BOTTOM] = bottom;

        return temp;
    }

}
