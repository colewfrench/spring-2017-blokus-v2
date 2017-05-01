package edu.up.cs301.blokus.actions;


import java.io.Serializable;

import edu.up.cs301.game.GamePlayer;
import edu.up.cs301.game.actionMsg.GameAction;

/**
 * Used to select a Blok on the selected piece in the GameState
 * @author Adrian Low
 * @author Cole French
 * @author Devin Ajimine
 * @author Evan Sterba
 */
public class SelectBlokOnSelectedPieceAction extends GameAction implements Serializable{

    private static final long serialVersionUID = 492017005L;

    private int selectedBlokId;

    /**
     * constructor
     *
     * @param player the player who created the action
     * @param selectedBlokId the blok ID on the piece that was selected;
     *          Player selects the corner on the Piece preview screen
     *          that will match up according to the corner that is on the board
     */
    public SelectBlokOnSelectedPieceAction(GamePlayer player, int selectedBlokId)
    {
        super(player);

        this.selectedBlokId = selectedBlokId;
    }

    public int getSelectedBlokId()
    {
        return this.selectedBlokId;
    }
}

