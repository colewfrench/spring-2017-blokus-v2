package edu.up.cs301.blokus.pieces;

import javax.xml.transform.Templates;

import edu.up.cs301.blokus.BlokusGameState;
import edu.up.cs301.blokus.PieceBlok;

/**
 * Piece Definition
 *
 * @author Devin Ajimine
 */
public class PieceL4 extends PieceTemplate
{
    public PieceL4()
    {
        super(PIECE_L4);

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
        switch (pieceBlokId)
        {
            case 0:
                left = 1;
                break;
            case 1:
                top = 2;
                right = 0;
                break;
            case 2:
                top = 3;
                bottom = 1;
                break;
            case 3:
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
