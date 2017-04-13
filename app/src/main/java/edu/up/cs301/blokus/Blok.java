package edu.up.cs301.blokus;

import java.io.Serializable;

/**
 * This class defines each square on the board, as well as being
 * the parent class for the blok that defines the Blokus pieces
 *
 * @author Adrian Low
 * @author Cole French
 * @author Devin Ajimine
 * @author Evan Sterba
 */
public class Blok implements Serializable{

    private static final long serialVersionUID = 492017001L;
    private int row, col, color, id; // the blok's row and column on the board array

    public Blok(int row, int col, int color, int id)
    {
        this.row = row;
        this.col = col;
        this.color = color;
        this.id = id;
    }

    public int getRow()
    {
        return row;
    }

    public int getColumn()
    {
        return col;
    }

    public int getColor()
    {
        return color;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getId()
    {
        return this.id;
    }

    public void setId(int newId)
    {
        this.id = newId;
    }
}

