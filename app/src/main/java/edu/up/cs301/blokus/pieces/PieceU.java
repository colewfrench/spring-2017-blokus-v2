package edu.up.cs301.blokus.pieces;

import edu.up.cs301.blokus.BlokusGameState;
import edu.up.cs301.blokus.PieceBlok;

/**
 * Created by lowa19 on 4/1/2017.
 */
public class PieceU extends PieceTemplate {
    public PieceU()
    {
        super(PIECE_U);

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
                bottom = 1;
                break;
            case 1:
                top = 0;
                right = 2;
                break;
            case 2:
                left = 1;
                right = 3;
                break;
            case 3:
                left = 2;
                top = 4;
                break;
            case 4:
                bottom = 3;
                break;
        }

        temp[PieceBlok.LEFT] = left;
        temp[PieceBlok.TOP] = top;
        temp[PieceBlok.RIGHT] = right;
        temp[PieceBlok.BOTTOM] = bottom;

        return temp;
    }
}
