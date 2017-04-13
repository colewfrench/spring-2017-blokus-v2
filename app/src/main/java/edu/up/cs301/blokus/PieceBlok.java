package edu.up.cs301.blokus;

import edu.up.cs301.blokus.Blok;

/**
 * This class defines the Blok objects that will constitute
 * a BlokusPiece object.
 *
 * @author Adrian Low
 * @author Cole French
 * @author Devin Ajimine
 * @author Evan Sterba
 */
public class PieceBlok extends Blok {

    public static final int LEFT = 0;
    public static final int TOP = 1;
    public static final int RIGHT = 2;
    public static final int BOTTOM = 3;

    private int[] adjacencies;

    public PieceBlok(int row, int col, int color, int id)
    {
        super(row, col, color, id);
        this.adjacencies = new int[4];
    }

    public int[] getAdjacencies()
    {
        return this.adjacencies;
    }

    public void setAdjacencies(int[] newAdj)
    {
        for (int i = 0; i < 4; i++)
        {
            this.adjacencies[i] = newAdj[i];
        }
    }
}
