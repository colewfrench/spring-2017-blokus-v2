package edu.up.cs301.blokus;

/**
 * This class defines the Blok objects that will constitute
 * a Blokus PieceTemplate object.
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
    private boolean hasCorner;
    private int[] adjacencies;

    /**
     * constructor
     *
     * @param row row
     * @param col column
     * @param color color of the piece
     * @param id each PieceBlok has an id based on PieceNames.png
     *
     */
    public PieceBlok(int row, int col, int color, int id)
    {
        super(row, col, color, id);
        this.adjacencies = new int[4];
        this.hasCorner = true;
    }//ctor

    //getters and setters
    public int[] getAdjacencies()
    {
        return this.adjacencies;
    }

    public void setAdjacencies(int[] newAdj)
    {
        System.arraycopy(newAdj, 0, adjacencies, 0, newAdj.length);
    }

    public boolean hasCorner()
    {
        return this.hasCorner;
    }

    public void setHasCorner(boolean newHasCorner)
    {
        this.hasCorner = newHasCorner;
    }
}
