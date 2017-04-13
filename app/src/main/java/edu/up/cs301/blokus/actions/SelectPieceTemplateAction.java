package edu.up.cs301.blokus.actions;


import java.io.Serializable;

import edu.up.cs301.blokus.pieces.PieceTemplate;
import edu.up.cs301.game.GamePlayer;
import edu.up.cs301.game.actionMsg.GameAction;

/**
 * Created by lowa19 on 3/5/2017.
 */
public class SelectPieceTemplateAction extends GameAction implements Serializable {

    private static final long serialVersionUID = 492017004L;

    private int selectedPieceID = -1;

    /**
     * constructor
     * @author Adrian Low
     * @author Cole French
     * @author Devin Ajimine
     * @author Evan Sterba
     * @param player the player who created this action
     * @param selectedPieceID the piece that was selected
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

