package edu.up.cs301.blokus.actions;

import java.io.Serializable;

import edu.up.cs301.game.GamePlayer;
import edu.up.cs301.game.actionMsg.GameAction;

/**
 *
 * When the player has no moves avaliable the doNothingAction
 * will skip to the next persons turn; also used so ComputerPlayers
 * can request a new GameState without modifying anything
 *
 * @author Cole French
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
