package edu.up.cs301.blokus.pieces;


import java.io.Serializable;

import edu.up.cs301.blokus.Blok;
import edu.up.cs301.blokus.PieceBlok;

/**
 * PieceTenmplate class is the template for the 21 pieces it
 * contains methods for all the actions that will be overridden
 * by each piece once their class is implemented
 *
 * NOTE: all PieceBlok adjacency ids are set based on definition
 * from PieceNames.png
 *
 *  @author Adrian Low
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

    protected PieceBlok[] pieceShape;
    protected int pieceId;
    protected PieceBlok anchor;

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

    protected abstract int[] getAdjArray(int pieceBlokId);
    public PieceBlok getAnchor()
    {
        return this.anchor;
    }

    public void setAnchor(PieceBlok anchor)
    {
        this.anchor = anchor;
    }
}

