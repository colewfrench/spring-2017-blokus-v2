package edu.up.cs301.blokus.pieces;

import edu.up.cs301.blokus.BlokusGameState;
import edu.up.cs301.blokus.PieceBlok;

/**
 * Created by ajimine19 on 4/1/2017.
 */
public class PieceX extends PieceTemplate
{
    public PieceX()
    {
        super(PIECE_X);

        PieceBlok[] myShape = new PieceBlok[5];

        for (int i = 0; i < 5; i++)
        {
            myShape[i] = new PieceBlok(0,0, BlokusGameState.EMPTY_BLOK, i);

            //Set Adjacencies for each blok using getAdjArray method
            myShape[i].setAdjacencies(getAdjArray(i));
            myShape[i].setHasCorner(isValidCorner(i));
        }

        setPieceShape(myShape);
        this.setAnchor(myShape[2]);
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
        switch (pieceBlokId)
        {
            case 0:
                top = 2;
                break;
            case 1:
                right = 2;
                break;
            case 2:
                top = 4;
                bottom = 0;
                left = 1;
                right = 3;
                break;
            case 3:
                left = 2;
                break;
            case 4:
                bottom = 2;
                break;

        }

        temp[PieceBlok.LEFT] = left;
        temp[PieceBlok.TOP] = top;
        temp[PieceBlok.RIGHT] = right;
        temp[PieceBlok.BOTTOM] = bottom;

        return temp;
    }
}
