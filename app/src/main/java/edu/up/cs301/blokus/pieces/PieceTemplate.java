package edu.up.cs301.blokus.pieces;


import java.io.Serializable;

import edu.up.cs301.blokus.Blok;
import edu.up.cs301.blokus.PieceBlok;

/**
 * PieceTemplate class is the template for the 21 pieces it
 * contains methods for all the actions that will be overridden
 * by each piece once their class is implemented
 *
 * NOTE: all PieceBlok adjacency ids are set based on definition
 * from PieceNames.png
 *
 * @author Adrian Low
 * @author Cole French
 * @author Devin Ajimine
 * @author Evan Sterba
 */
public abstract class PieceTemplate implements Serializable{

    protected static final long serialVersionUID = 492017002L;

    public static final int NO_ADJ = -1;

    public static final int PIECE_I1 = 0;
    public static final int PIECE_I2 = 1;
    public static final int PIECE_V3 = 2;
    public static final int PIECE_I3 = 3;
    public static final int PIECE_O4 = 4;
    public static final int PIECE_T4 = 5;
    public static final int PIECE_I4 = 6;
    public static final int PIECE_L4 = 7;
    public static final int PIECE_Z4 = 8;
    public static final int PIECE_L5 = 9;
    public static final int PIECE_T = 10;
    public static final int PIECE_Y = 11;
    public static final int PIECE_X = 12;
    public static final int PIECE_U = 13;
    public static final int PIECE_W = 14;
    public static final int PIECE_P = 15;
    public static final int PIECE_I5 = 16;
    public static final int PIECE_Z5 = 17;
    public static final int PIECE_N = 18;
    public static final int PIECE_V5 = 19;
    public static final int PIECE_F = 20;

    protected PieceBlok[] pieceShape; // the Bloks that define the piece
    protected int pieceId; // the piece's unique id number
    protected PieceBlok anchor; // the Blok in the piece used to rotate and flip around

    public PieceTemplate(int pieceId)
    {
        this.pieceId = pieceId;
        this.pieceShape = null;
        this.anchor = null;
    }

    public void setPieceShape(PieceBlok[] pieceShape)
    {
        this.pieceShape = pieceShape;
    }

    public int getPieceId()
    {
        return this.pieceId;
    }

    public PieceBlok[] getPieceShape()
    {
        return pieceShape;
    }

    /**
     * perform the matrix element swaps to flip the piece
     */
    public void flip()
    {
        int[] curAdj;
        int temp;

        for (int i = 0; i < pieceShape.length; i++)
        {
            curAdj = pieceShape[i].getAdjacencies();
            temp = curAdj[0];
            curAdj[0] = curAdj[2];
            curAdj[2] = temp;
            pieceShape[i].setAdjacencies(curAdj);
        }
    }

    /**
     * perform the matrix element swaps to rotate the piece
     */
    public void rotate()
    {
        int[] curAdj;
        int temp;

        for (int i = 0; i < pieceShape.length; i++)
        {
            curAdj = pieceShape[i].getAdjacencies();
            temp = curAdj[3];
            curAdj[3] = curAdj[2];
            curAdj[2] = curAdj[1];
            curAdj[1] = curAdj[0];
            curAdj[0] = temp;

            pieceShape[i].setAdjacencies(curAdj);
        }
    }

    /**
     * used to set which Bloks on the piece are adjacent with
     * each other Blok on the piece; this is what defines
     * the shape of each piece
     * @param pieceBlokId the Blok having its adjacencies set
     * @return the given Blok's newly set adjacency matrix
     */
    protected abstract int[] getAdjArray(int pieceBlokId);

    /**
     * This method helps determine if the selected Blok on the piece
     * can be used as a corner Blok to place against any corner
     * on the board
     *
     * @param pieceBlokId indicates which Blok in the piece definition to check
     * @return true if the Blok being checked has a corner on the piece
     */
    protected abstract boolean isValidCorner(int pieceBlokId);

    public PieceBlok getAnchor()
    {
        return this.anchor;
    }

    public void setAnchor(PieceBlok anchor)
    {
        this.anchor = anchor;
    }
}

