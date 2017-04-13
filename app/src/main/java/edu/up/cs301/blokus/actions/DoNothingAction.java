package edu.up.cs301.blokus.actions;

import java.io.Serializable;

import edu.up.cs301.game.GamePlayer;
import edu.up.cs301.game.actionMsg.GameAction;

/**
 * Created by sterba19 on 4/9/2017.
 */

public class DoNothingAction extends GameAction implements Serializable {

    private static final long serialVersionUID = 4920170009L;

    private boolean passMyTurn;

    public DoNothingAction(GamePlayer player, boolean passTurn)
    {
        super(player);
        this.passMyTurn = passTurn;
    }

    public boolean passMyTurn()
    {
        return passMyTurn;
    }
}
