package edu.up.cs301.blokus.actions;


import java.io.Serializable;

import edu.up.cs301.blokus.Blok;
import edu.up.cs301.blokus.BlokusGameState;
import edu.up.cs301.blokus.PieceBlok;
import edu.up.cs301.blokus.pieces.PieceTemplate;
import edu.up.cs301.game.GamePlayer;
import edu.up.cs301.game.actionMsg.GameAction;

/**
 * ConfirmPiecePlacementAction
 *
 * Used to confirm that a piece is in the users desired position will only
 * be used by the human player
 *  @author Adrian Low
 * @author Cole French
 * @author Devin Ajimine
 * @author Evan Sterba
 */
public class ConfirmPiecePlacementAction extends GameAction implements Serializable{

    private static final long serialVersionUID = 492017007L;

    /**
     * constructor
     *
     * @param player the player who created the action
     */
    public ConfirmPiecePlacementAction(GamePlayer player)
    {
        super(player);
    }
}

