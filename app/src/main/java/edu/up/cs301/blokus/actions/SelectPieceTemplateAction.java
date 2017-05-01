package edu.up.cs301.blokus.actions;


import java.io.Serializable;

import edu.up.cs301.game.GamePlayer;
import edu.up.cs301.game.actionMsg.GameAction;

/**
 * Used to select a piece in the LocalGame's GameState
 *
 * @author Adrian Low
 * @author Cole French
 * @author Devin Ajimine
 * @author Evan Sterba
 */
public class SelectPieceTemplateAction extends GameAction implements Serializable {

    private static final long serialVersionUID = 492017004L;

    private int selectedPieceID = -1;

    /**
     * constructor
     *
     * @param player the player who created this action
     * @param selectedPieceID the piece that was selected
     *              Player selects the piece they want to display on the board,
     *              which will highlight when selected
     */
    public SelectPieceTemplateAction(GamePlayer player, int selectedPieceID)
    {
        super(player);

        this.selectedPieceID = selectedPieceID;
    }

    public int getSelectedPieceID()
    {
        return this.selectedPieceID;
    }
}

