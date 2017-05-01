package edu.up.cs301.blokus.actions;

import java.io.Serializable;

import edu.up.cs301.game.GamePlayer;
import edu.up.cs301.game.actionMsg.GameAction;

/**
 * Used to rotate the selected piece in the GameState.
 *
 * @author Adrian Low
 * @author Cole French
 * @author Devin Ajimine
 * @author Evan Sterba
 */
public class RotateSelectedPieceAction extends GameAction implements Serializable{

    private static final long serialVersionUID = 492017006L;

    /**
     * constructor
     *
     * @param player the player who created the action
     */
    public RotateSelectedPieceAction(GamePlayer player) {
        super(player);
    }
}

