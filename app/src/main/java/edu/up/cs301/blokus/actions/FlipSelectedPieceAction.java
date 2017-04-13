package edu.up.cs301.blokus.actions;


import java.io.Serializable;

import edu.up.cs301.blokus.pieces.PieceTemplate;
import edu.up.cs301.game.GamePlayer;
import edu.up.cs301.game.actionMsg.GameAction;

/**
 * Created by lowa19 on 3/5/2017.
 */
public class FlipSelectedPieceAction extends GameAction implements Serializable{

    private static final long serialVersionUID = 492017007L;

    /**
     * constructor
     *
     * @param player the player who created the action
     *
     * @author Adrian Low
     * @author Cole French
     * @author Devin Ajimine
     * @author Evan Sterba
     */
    public FlipSelectedPieceAction(GamePlayer player) {
        super(player);
    }
}

