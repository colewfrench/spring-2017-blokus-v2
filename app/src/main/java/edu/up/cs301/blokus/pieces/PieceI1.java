package edu.up.cs301.blokus.pieces;


import edu.up.cs301.blokus.BlokusGameState;
import edu.up.cs301.blokus.PieceBlok;

/**
 * PieceI1- first piece 1 by 1
 * @author Adrian Low
 * @author Cole French
 * @author Devin Ajimine
 * @author Evan Sterba
 *
 */
public class PieceI1 extends PieceTemplate {

    public PieceI1()
    {
        super(PIECE_I1);

        PieceBlok[] myShape = new PieceBlok[1];
        myShape[0] = new PieceBlok(0,0, BlokusGameState.EMPTY_BLOK, 0);
        myShape[0].setAdjacencies(getAdjArray(0));
        myShape[0].setHasCorner(isValidCorner(0));

        setPieceShape(myShape);
        this.setAnchor(myShape[0]);
    }

    @Override
    protected boolean isValidCorner(int pieceBlokId)
    {
        switch (pieceBlokId)
        {
            default:
                return true;
        }
    }

    @Override
    protected int[] getAdjArray(int pieceBlokId)
    {
        int[] adj = new int[4];

        for (int i = 0; i < 4; i++)
        {
            adj[i] = NO_ADJ;
        }

        return adj;
    }
}

